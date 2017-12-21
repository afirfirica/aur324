package aurora.android.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import aurora.android.R;

import butterknife.ButterKnife;
import ice.caster.android.shout.Config;
import ice.caster.android.shout.Encoder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BroadcastFragment extends Fragment implements View.OnClickListener {

    /**
     * Icecast host
     */
    static final String ICE_HOST    = "188.214.104.105";

    /**
     * Broadcast port that server listens incoming streams
     */
    static final int    ICE_PORT    = 8000;

    /**
     * Mount point of incoming source
     */
    static final String ICE_MOUNT   = "/test";

    /**
     * Credentials
     */
    static final String ICE_USER    = "source";
    static final String ICE_PASS    = "hackme";

    @BindView(R.id.start)
    Button start;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.status)
    TextView status;

    private Encoder encoder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encoder = new Encoder(
                new Config().host(ICE_HOST).port(ICE_PORT).mount(ICE_MOUNT)
                        .username(ICE_USER).password(ICE_PASS).sampleRate(8000));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * This is short-hand setter and handler should be static class
         */
        encoder.setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Encoder.MSG_REC_STARTED:
                        status.setText("Streaming");
                        break;
                    case Encoder.MSG_REC_STOPPED:
                        status.setText("");
                        break;
                    case Encoder.MSG_ERROR_GET_MIN_BUFFERSIZE:
                        status.setText("");
                        Toast.makeText(getActivity(),
                                "MSG_ERROR_GET_MIN_BUFFERSIZE",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Encoder.MSG_ERROR_REC_START:
                        status.setText("");
                        Toast.makeText(getActivity(), "Can not start recording",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Encoder.MSG_ERROR_AUDIO_RECORD:
                        status.setText("");
                        Toast.makeText(getActivity(), "Error audio record",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Encoder.MSG_ERROR_AUDIO_ENCODE:
                        status.setText("");
                        Toast.makeText(getActivity(), "Error audio encode",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Encoder.MSG_ERROR_STREAM_INIT:
                        status.setText("");
                        Toast.makeText(getActivity(), "Can not init stream",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        });

        start.setOnClickListener(this);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encoder.stop();
            }
        });
    }

    public void onClick(View v) {
        BroadcastFragmentPermissionsDispatcher.startEncoderWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void startEncoder() {
        encoder.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        BroadcastFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}