package org.xuxiaoxiao.xiao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xuxiaoxiao.xiao.base.BaseFragment;
import org.xuxiaoxiao.xiao.chatconfig.ChatConfigActivity;
import org.xuxiaoxiao.xiao.infrastructure.EBHiddFuncPanel;
import org.xuxiaoxiao.xiao.infrastructure.Internet;
import org.xuxiaoxiao.xiao.infrastructure.SendEmotion;
import org.xuxiaoxiao.xiao.infrastructure.ToggleFunctionPanel;
import org.xuxiaoxiao.xiao.model.ChatMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.app.Activity.RESULT_OK;

//import android.media.SoundPool;

/**
 * Created by WuQiang on 2017/4/10.
 */

public class ChatFragment extends BaseFragment {
    /**
     * private String mUsername;
     * private SyncReference mWilddogRef;
     * private ValueEventListener mConnectedListener;
     * private ChatListAdapter mChatListAdapter;
     */


    private String mUsername;
    private SyncReference mWilddogRef;
    private ValueEventListener mConnectedListener;
    //    private MessageAdapter mChatListAdapter;
    private EditText inputText;

    // 一个实现 Callbacks 的对象，
    // 将来会 在 onAttach 方法当中 把 Activity 转换成 Callbacks
    // 并赋给这个变量
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;

    public static final int MEDIA_TYPE_TEXT = 0;
    public static final int MEDIA_TYPE_PHOTO = 1;
    // 图库当中选择图片时使用的一个 ResultCode
    private static final int RESULT_LOAD_IMAGE = 9002;
    // 下面这个 Bitmap 是图片在发送失败之后，再次发送使用的
    private Bitmap phothBitmap;
    // 全局性的一个信息，在 ContextMenu 菜单当中使用
    private ChatMessage mGlobalChatMessage;
    private ImageButton sendTextButton;
    private ImageButton imgSelectButton;
    // 下面是与把聊天记录保存到手机上有关的
    private File fileToEdit;
    private LoadTextTask loadTask = null;
    private boolean loaded = false;

    /**
     * Required interface for hosting activities.
     * <p>
     * private Callbacks mCallbacks;
     * <p>
     * public interface Callbacks {
     * // 接口定义了需要 Activity 做的事情
     * // 只要一个 Activity实现了这个接口
     * // Fragment 就有了可以调用 Activity 当中 函数的办法
     * //        void onCrimeSelected(Crime crime);
     * void onFunctionPanelSelected();
     * void hideFunctionPanel();
     * }
     *
     * @Override // 注意传进来的是 Context
     * public void onAttach(Context context) {
     * super.onAttach(context);
     * mCallbacks = (Callbacks) context;
     * }
     * @Override public void onDetach() {
     * super.onDetach();
     * mCallbacks = null;
     * }
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        wUser.getEmail();
        // Make sure we have a mUsername
//        setupUsername();

//        setTitle("Chatting as " + mUsername);

        // Setup our Wilddog mWilddogRef
        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
//        new DownloadTask().execute();
//        new PutBmob().execute();
//        new PutBmob().execute();

//        InputStream in = getActivity().getApplicationContext().getResources().openRawResource(R.drawable.imgdemo);
        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= 19) {
            // 目录 加 文件名 可以创建一个 File
            // https://developer.android.com/training/basics/data-storage/files.html
            fileToEdit = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"xiao.txt");
        }else{
            fileToEdit = new File(Environment.getExternalStorageDirectory() + "/Documents","xiao.txt");
        }
        if(!fileToEdit.exists())
            fileToEdit.getParentFile().mkdirs();
        // fileToEdit.getParentFile().mkdirs()
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateUI();
//        Log.d("WQ",mChatListAdapter.toString());
//        Log.d("WQ",ListView.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        messageAdapter.setup();
        // Finally, a little indication of connection status
        mConnectedListener = mWilddogRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(getActivity(), "Connected to Wilddog", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Disconnected from Wilddog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(SyncError wilddogError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mWilddogRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        // 不要 messageAdapter.cleanup(); 程序 就不会Crash 不知道为什么
//        messageAdapter.cleanup();
    }

    @Override
    public void onDestroy() {
        if (loadTask != null) {
            loadTask.cancel(false);
        }

        super.onDestroy();
    }

//    private void setupUsername() {
//        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
//        mUsername = prefs.getString("username", null);
//        if (mUsername == null) {
//            Random r = new Random();
//            // Assign a random user name if we don't have one saved.
//            mUsername = "WilddogUser" + r.nextInt(100000);
//            prefs.edit().putString("username", mUsername).commit();
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_conf, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 转到配置页面
            case R.id.chat_config:
//                Toast.makeText(getActivity(),"dddd",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ChatConfigActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void sendMessage() {
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            // Create a new, auto-generated child of that chat location, and save our chat data there
            String key = mWilddogRef.push().getKey();
            ChatMessage chat = new ChatMessage(input, user.getName(), key, 0, "T");
            mWilddogRef.child(key).setValue(chat);
            inputText.setText("");
        }
    }

    /**
     *  mWilddogRef.push().setValue("Test1"); 
     *  ChatMessage chat = new ChatMessage(input, user.getName(), “这是消息内容”, 0, "T"); 
     *  mWilddogRef.child("Test1").setValue(chat);
     * @param event
     */
    /*
    public void sendEmotion(String emotionName) {
        String key = mWilddogRef.push().getKey();
        ChatMessage chat = new ChatMessage(emotionName, user.getName(), key,1);
//            Log.d("WQ_ChatFragment", key);
        mWilddogRef.child(key).setValue(chat);
        inputText.setText("");
    }
    */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SendEmotion event) {
        String key = mWilddogRef.push().getKey();
        ChatMessage chat = new ChatMessage(event.getEmotionName(), user.getName(), key, 1, "");
//            Log.d("WQ_ChatFragment", key);
        mWilddogRef.child(key).setValue(chat);
        inputText.setText("");
    }

    public void showKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这儿是因为有三个文件吗？要用线程池
        if (!loaded) {
            // 在创建 View 的时候，如果还没有 load 那就把三个文件载入
            loadTask = new LoadTextTask();
            loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    (File)fileToEdit);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.chat_top_fragment, container, false);
        messageRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler_view);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

//        if (!loaded) {
//            // 在创建 View 的时候，如果还没有 load 那就把三个文件载入
//            loadTask = new LoadTextTask();
//            loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(File)fileToEdit);
//        }

        Button sendEmotion = (Button) view.findViewById(R.id.function_button);
        imgSelectButton = (ImageButton) view.findViewById(R.id.img_button);
        imgSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        sendEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击，跳出表情包部分
                hideKeyboard(view);
                inputText.clearFocus();
                // RecyclerView 拉到最底
                messageAdapter.notifyDataSetChanged();
//                mCallbacks.onFunctionPanelSelected();
                EventBus.getDefault().post(new ToggleFunctionPanel());
            }
        });


        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
//        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        // registerAdapterDataObserver

        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                messageRecyclerView.scrollToPosition((messageAdapter.getItemCount() - 1));
            }
        });

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        inputText = (EditText) view.findViewById(R.id.message_input);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int textSum = s.toString().length();
                if (textSum != 0) {
                    sendTextButton.setVisibility(View.VISIBLE);
                    imgSelectButton.setVisibility(View.GONE);
                } else {
                    sendTextButton.setVisibility(View.GONE);
                    imgSelectButton.setVisibility(View.VISIBLE);
                }
            }
        });
        sendTextButton = (ImageButton) view.findViewById(R.id.send_text_button);
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        inputText = (EditText) view.findViewById(R.id.message_input);
        inputText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            // 输入信息文本获得焦点时
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
//                    mHiddenView.setVisibility(View.GONE);
//                    mCallbacks.hideFunctionPanel();
                    EventBus.getDefault().post(new EBHiddFuncPanel());
                    // 把 RecyclerView 当中的信息拉到最低
                    messageAdapter.notifyDataSetChanged();

                } else {
                    // 此处为失去焦点时的处理内容
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public void updateUI() {
        // 这个方法是在 onResume 方法当中调用的，
        // 也就是让整个 View 在页面重新呈现的时候
        // 用全新的数据刷新一下，
        // 因为所有的操作其实都是针对  Model(数据) 的，
        // 所以 Model 变化之后，View也应该及时的刷新
//        CrimeLab crimeLab = CrimeLab.get(getActivity());
//        List<Crime> crimes = crimeLab.getCrimes();

        if (messageAdapter == null) {
//            mWilddogRef.orderByKey().endAt("-KhwMNoo8b8dLA7BGOHd").limitToLast(20)
            // 下面这一句是可行的，成功了
//            messageAdapter = new MessageAdapter(mWilddogRef.orderByKey().endAt("-KhwMNoo8b8dLA7BGOHd").limitToLast(20));
            messageAdapter = new MessageAdapter(mWilddogRef.limitToLast(20));
            messageRecyclerView.setAdapter(messageAdapter);
        } else {
//            mAdapter.setCrimes(crimes);
            // 让所有的数据都更新，非常没有效率
            messageAdapter.notifyDataSetChanged();
        }
        // 更新 UI 的时候，连同 Subtitle 一起更新
//        updateSubtitle();
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        private Query mRef;
        private List<ChatMessage> mModels;
        private List<String> mKeys;
        private ChildEventListener mListener;


        public MessageAdapter(Query mRef) {
            this.mRef = mRef;

            mModels = new ArrayList<>();
            mKeys = new ArrayList<String>();
            // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
            // 注意事件类型，在这儿是 ChildEventListener
            // 注意下边是定义了一个事件类型，可以像变量一样使用
            mListener = this.mRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    // Calling val() / getValue() on a snapshot returns the JavaScript object representation of the data.
                    // If no data exists at the reference's location, the snapshots value will be null.
                    ChatMessage model = (ChatMessage) dataSnapshot.getValue(ChatMessage.class);
                    String key = dataSnapshot.getKey();
                    Log.d("WQWQ", String.valueOf(key));
                    // Insert into the correct location, based on previousChildName
                    // 如果这是第一条消息
                    if (previousChildName == null) {
                        mModels.add(0, model);
                        mKeys.add(0, key);
                    } else {
                        // 从 key 数组当中 获得上一个消息的 key 的 索引
                        int previousIndex = mKeys.indexOf(previousChildName);
//                        Toast.makeText(getActivity(),String.valueOf(previousIndex),Toast.LENGTH_SHORT).show();
                        // 把这个索引加 1
                        int nextIndex = previousIndex + 1;
                        // 如果数组（消息数组，比如我们设置的是20条消息）已经满了，那就直接 append 到最后
                        if (nextIndex == mModels.size()) {
                            mModels.add(model);
                            mKeys.add(key);
                        } else {
                            // 这是消息还没有满的情况
                            mModels.add(nextIndex, model);
                            mKeys.add(nextIndex, key);
                        }
                    }
                    notifyDataSetChanged();
//                    if (application.isAlarmOn) {
//                        soundPool.playSoundDestroy();
//
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // One of the mModels changed. Replace it in our list and name mapping
                    String key = dataSnapshot.getKey();
                    ChatMessage newModel = (ChatMessage) dataSnapshot.getValue(ChatMessage.class);
                    int index = mKeys.indexOf(key);

                    mModels.set(index, newModel);

                    notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    // A model was removed from the list. Remove it from our list and the name mapping
                    String key = dataSnapshot.getKey();
                    int index = mKeys.indexOf(key);

                    mKeys.remove(index);
                    mModels.remove(index);

                    notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                    // A model changed position in the list. Update our list accordingly
                    String key = dataSnapshot.getKey();
                    ChatMessage newModel = (ChatMessage) dataSnapshot.getValue(ChatMessage.class);
                    int index = mKeys.indexOf(key);
                    mModels.remove(index);
                    mKeys.remove(index);
                    if (previousChildName == null) {
                        mModels.add(0, newModel);
                        mKeys.add(0, key);
                    } else {
                        int previousIndex = mKeys.indexOf(previousChildName);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mModels.size()) {
                            mModels.add(newModel);
                            mKeys.add(key);
                        } else {
                            mModels.add(nextIndex, newModel);
                            mKeys.add(nextIndex, key);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(SyncError syncError) {
                    Log.e("WilddogListAdapter", "Listen was cancelled, no more updates will occur");
                }

            });
        }

        public void cleanup() {
            // We're being destroyed, let go of our mListener and forget about all of the mModels
            mRef.removeEventListener(mListener);
            mModels.clear();
            mKeys.clear();
        }

        public void setup() {
            // We're being destroyed, let go of our mListener and forget about all of the mModels
            mRef.addChildEventListener(mListener);
            mModels.clear();
            mKeys.clear();
        }

        @Override
        public int getItemViewType(int position) {
            // 可以在这儿根据 position 知道是不是自己的消息，如果是自己的消息，以后就不 CreateViewHolder 了
            return mModels.get(position).getMediaType();
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0: {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.media_type_text, parent, false);
                    return new TextMessageViewHolder(view);
                }

                case 1: {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.media_type_photo, parent, false);
                    return new PhotoMessageViewHolder(view);
                }
                default:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
//            Toast.makeText(getActivity(),String.valueOf(position),Toast.LENGTH_SHORT).show();
//            if (position == 3){
//                // 如果RecyclerView已经快拉到最上面了，也就是快没有消息了
//                // 获取位置上的消息ID
////                String messageID = mModels.get(position).getMessageID();
//////                mRef.removeEventListener(mListener);
////                mRef.orderByKey();
////                mRef.endAt(messageID).limitToLast(20);
////                mRef.addChildEventListener(mListener);
//
////                new MessageAdapter(mWilddogRef.orderByKey().endAt(messageID).limitToLast(20));
//                // 再往上拉20条消息出来
//                // 在拉之前必须先以chat为基础 order 一下
////                Query queryRef =  mWilddogRef.child(messageID);
////                mWilddogRef.orderByKey().startAt(messageID).limitToLast(20);
////                mWilddogRef.orderByKey();
////                mWilddogRef.limitToLast(20).startAt(messageID);
////                notifyDataSetChanged();
////                mModels.clear();
////                mKeys.clear();
//            }
//            if (position == 0){
//                String messageID = mModels.get(position).getMessageID();
//
//                Query temp = mRef.endAt(messageID).limitToLast(20);
//                temp.addChildEventListener(mListener);
//            }
            // 如果是文本类型的消息的话
            if (holder.getItemViewType() == 0) {
                TextMessageViewHolder textViewHolder = (TextMessageViewHolder) holder;
                textViewHolder.bind(mModels.get(position));
            } else {
                PhotoMessageViewHolder textViewHolder = (PhotoMessageViewHolder) holder;
                textViewHolder.bind(mModels.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }
    }

    private abstract class MessageViewHolder extends RecyclerView.ViewHolder {
//        private TextView msgID;
//        private LinearLayout layout;
//        private ChatMessage mChatMessage;

        public MessageViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(ChatMessage chatMessage);

    }

    private class TextMessageViewHolder extends MessageViewHolder implements View.OnCreateContextMenuListener {
        private TextView msg;

        private TextView msgID;

        private LinearLayout layout;

        private ChatMessage mChatMessage;


        public TextMessageViewHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.msg);

            msgID = (TextView) itemView.findViewById(R.id.msgid);

            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            itemView.setOnCreateContextMenuListener(this);
//            itemView.setOnClickListener(this);
        }

        public void bind(ChatMessage chatMessage) {
            mChatMessage = chatMessage;
            boolean isMine = mChatMessage.getAuthor() != null && mChatMessage.getAuthor().equals(user.getName());

            msg.setText(mChatMessage.getMessage());
            msgID.setText(mChatMessage.getMessageID());

            layout.setBackgroundResource(isMine ? R.drawable.message_right : R.drawable.message_left);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (!isMine) {
                params.gravity = Gravity.LEFT;
            } else {
                params.gravity = Gravity.RIGHT;
            }
            layout.setLayoutParams(params);
            new SaveThread(mChatMessage.getMessage(),(File)fileToEdit).start();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            // 把 Message 赋给一个全局变量，这样可以在右键菜单当中使用
            mGlobalChatMessage = mChatMessage;
            menu.setHeaderTitle("Select The Action");
            menu.add(0, 1, 0, "Message");//groupId, itemId, order, title
            menu.add(0, 2, 0, "删除消息");
            menu.add(0, 3, 0, "更改");
            menu.add(0, 4, 0, "复制");
        }
    }

    // 右键菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView .AdapterContextMenuInfo) item.getMenuInfo();
//        ContextMenu.ContextMenuInfo ddd = item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getActivity(), mGlobalChatMessage.getMessage(), Toast.LENGTH_SHORT).show();
                //editNote(info.id);
                return true;
            case 2:
                mWilddogRef.child(mGlobalChatMessage.getMessageID()).removeValue();
//                Toast.makeText(getActivity(), mGlobalChatMessage.getAuthor(), Toast.LENGTH_SHORT).show();
                //shareNote(info.id);
                return true;
            case 3:
//                Toast.makeText(getActivity(), mGlobalChatMessage.getMessageID(), Toast.LENGTH_SHORT).show();
                HashMap<String, Object> user = new HashMap<>();
                user.put("message", "Tim Cook");
                mWilddogRef.child(mGlobalChatMessage.getMessageID()).updateChildren(user);
                //deleteNote(info.id);
                return true;
            case 4:
//                Toast.makeText(getActivity(),String.valueOf(mGlobalChatMessage.getMediaType()),Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    final android.content.ClipData clipData = android.content.ClipData
                            .newPlainText("text label", mGlobalChatMessage.getMessage());
                    clipboardManager.setPrimaryClip(clipData);
                } else {
                    @SuppressWarnings("deprecation")
                    final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setText(mGlobalChatMessage.getMessage());
                }
                //return super.onContextItemSelected(item);
                return true;
            case 5:
                Toast.makeText(getActivity(), "这是图片", Toast.LENGTH_SHORT).show();
                //return super.onContextItemSelected(item);
                return true;
            case 6:
                new PutBmob(phothBitmap, "test3").execute();
                Toast.makeText(getActivity(), "重新发送", Toast.LENGTH_SHORT).show();
                //return super.onContextItemSelected(item);
                return true;
            default:
                Toast.makeText(getActivity(), "Nothing", Toast.LENGTH_SHORT).show();
                //return super.onContextItemSelected(item);
                return false;
        }
    }

    private class PhotoMessageViewHolder extends MessageViewHolder implements View.OnCreateContextMenuListener {
        private ImageView imageView;
        private TextView msgID;

        private LinearLayout layout;

        private ChatMessage mChatMessage;

        public PhotoMessageViewHolder(View itemView) {
            super(itemView);

//            msgID = (TextView) itemView.findViewById(R.id.msgid);

            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        public void bind(ChatMessage chatMessage) {
            mChatMessage = chatMessage;
            boolean isMine = mChatMessage.getAuthor() != null && mChatMessage.getAuthor().equals(user.getName());
            if (mChatMessage.getImgUrl() == "") {
//                Toast.makeText(getActivity(),"发送失败",Toast.LENGTH_SHORT).show();
                // 发送失败之后，如果用户决定再发送一次，那应该取得这个消息的ID，
                // 然后把这条消息删除掉，然后再发送一次
                imageView.setImageResource(android.R.drawable.stat_notify_error);
            } else {
                Picasso.with(getActivity()).load(mChatMessage.getImgUrl())
                        .fit().centerCrop()
                        .placeholder(R.drawable.imgdemo)
                        .error(R.drawable.imgdemo).into(imageView);
            }
            layout.setBackgroundResource(isMine ? R.drawable.message_right : R.drawable.message_left);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (!isMine) {
                params.gravity = Gravity.LEFT;
            } else {
                params.gravity = Gravity.RIGHT;
            }
            layout.setLayoutParams(params);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            // 如果发送失败，这儿就加上一个重新发送的选项
            mGlobalChatMessage = mChatMessage;
            menu.setHeaderTitle("Select The Action");
            menu.add(0, 5, 0, "Photo");//groupId, itemId, order, title
            menu.add(0, 6, 0, "重新发送");
            menu.add(0, 7, 0, "ID");
            menu.add(0, 8, 0, "Type");
        }

//        @Override
//        public void onClick(View v) {
//            Toast.makeText(getActivity(),mChatMessage.getMessage(),Toast.LENGTH_SHORT).show();
//        }
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new Internet().fetchItems();
            return null;
        }
    }

    private class PutBmob extends AsyncTask<Void, Void, String> {
        Bitmap image;
        String name;

        public PutBmob(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {

            return new Internet().BmobPostPhoto(image, name);
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Toast.makeText(getActivity(),"发送成功",Toast.LENGTH_SHORT).show();
            String key = mWilddogRef.push().getKey();
            ChatMessage chat = new ChatMessage("", user.getName(), key, 1, s);
//            Log.d("WQ_ChatFragment", key);
            mWilddogRef.child(key).setValue(chat);
            inputText.setText("");
        }
    }

    private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Hold the bite representation of the image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            // Have a String representation of the image
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 在图库当中选择图片
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            phothBitmap = null;
//            imageToUplaod.setImageURI(null);
//            imageToUplaod.setImageURI(selectedImage);
//            bSelectImage.setText("Change Image");
            try {
                phothBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                new PutBmob(phothBitmap, "test3").execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    return new Internet().BmobPostPhoto(image, name);
    private class LoadTextTask extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... files) {
            String result = null;

            if (files[0].exists()) {
                BufferedReader br;

                try {
                    br = new BufferedReader(new FileReader(files[0]));

                    try {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append("\n");
                            line = br.readLine();
                        }

                        result = sb.toString();
                    } finally {
                        br.close();
                    }
                } catch (IOException e) {
                    Log.e(getClass().getSimpleName(), "Exception reading file", e);
                }
            }

            return (result);
        }

        @Override
        protected void onPostExecute(String s) {
//            editor.setText(s);
            loadTask = null;
            loaded = true;
        }
    }

    private static class SaveThread extends Thread {
        // 开启一个线程
        // 保存聊天记录到本地
        private static Lock lock = new ReentrantLock();
        private final String text;
        private final File fileToEdit;

        SaveThread(String text, File fileToEdit) {
            this.text = text;
            this.fileToEdit = fileToEdit;
        }

        @Override
        public void run() {
            // 加上同步锁，防止多次写入
            lock.lock();
            try {
//                fileToEdit.getParentFile().mkdirs();
                // 加上 true 就是追加内容
                FileOutputStream fos = new FileOutputStream(fileToEdit,true);

                Writer w = new BufferedWriter(new OutputStreamWriter(fos));

                try {
                    w.write(text + "\n");
                    w.flush();
                    fos.getFD().sync();
                } finally {
                    w.close();
                    // 释放同步锁
                    lock.unlock();
                }
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception writing file", e);
            }
        }
    }
}

/**
 * mChatMessage = chatMessage;
 * boolean isMine = mChatMessage.getAuthor() != null && mChatMessage.getAuthor().equals(user.getName());
 * <p>
 * msgID.setText(String.valueOf(chatMessage.getMessageID()));
 * <p>
 * layout.setBackgroundResource(isMine ? R.drawable.message_right : R.drawable.message_left);
 * LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
 * LinearLayout.LayoutParams.WRAP_CONTENT);
 * <p>
 * if (!isMine) {
 * params.gravity = Gravity.LEFT;
 * } else {
 * params.gravity = Gravity.RIGHT;
 * }
 * layout.setLayoutParams(params);
 */
