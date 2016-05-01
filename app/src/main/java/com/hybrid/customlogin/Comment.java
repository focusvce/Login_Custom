package com.hybrid.customlogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Comment extends Fragment {

    TextView t;
    String te="";
    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    EditText comment_text;
    Button button_comment;
    //private static final String ADD_COMMENT_URL = Utility.getIPADDRESS()+"add_comment.php";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            te = Integer.toString(getArguments().getInt("key"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_comment, container, false);
        //t = (TextView) view.findViewById(R.id.textView);
        //t.setText(te);
        comment_text = (EditText) view.findViewById(R.id.comment_input);
        button_comment = (Button) view.findViewById(R.id.button_comment);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_comment);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_comment);
        getComments(recyclerView, swipeRefreshLayout);
        button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackgroundTask backgroundTask = new BackgroundTask(getActivity());
                if(!comment_text.getText().toString().equals(""))
                backgroundTask.execute("add_comment",comment_text.getText().toString(),te);
                else
                    Toast.makeText(getContext(),"No Text Entered",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public void getComments(RecyclerView rec,SwipeRefreshLayout swi){
        BackgroundTask backgroundTask;
        backgroundTask = new BackgroundTask(getContext(), rec, swi);
        backgroundTask.execute("get_comments",Integer.toString(getArguments().getInt("key")));
    }


    public interface OnFragmentInteractionListener {
    }
}
