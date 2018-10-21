package com.hyphenate.easeui.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;

    static UserResultDto resultDto;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.em_default_avatar)
                        .error(R.drawable.em_default_avatar)
                        .centerCrop();
                Glide.with(context).load(user.getAvatar()).apply(options).into(imageView);
            } catch (Exception e) {
                //use default avatar
//                RequestOptions options = new RequestOptions()
//                        .placeholder(R.drawable.em_default_avatar)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL);
//                Glide.with(context).load(user.getAvatar()).apply(options).into(imageView);
                getNameAndHeadPost(username,imageView,context);
            }
        }else{
            Glide.with(context).load(R.drawable.em_default_avatar).into(imageView);
            getNameAndHeadPost(username,imageView,context);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
            EaseUser user = getUserInfo(username);
            if(user != null && user.getNick() != null){
//                textView.setText(user.getNick());
                getNameAndHeadPost(username,textView);
            }else{
//                textView.setText(username);
                getNameAndHeadPost(username,textView);
            }
        }
    }

    public static void setUserNick(String username,String nickrname){
        EaseUser user = getUserInfo(username);
        if(user != null ){
            user.setNickname(nickrname);
        }

    }

    public static void setUserAvatar(String username, String url){
        EaseUser user = getUserInfo(username);
        if(user != null ){
            user.setAvatar(url);
        }
    }

    public static void setUserNick(String username,TextView textView,String nickrname){
        if(textView != null){
            EaseUser user = getUserInfo(username);
            if(user != null && user.getNick() != null){
                textView.setText(user.getNick());
            }else{
                textView.setText(username);
            }
        }
    }

    public static void setUserAvatar(Context context, String username, ImageView imageView,String url){
        EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.em_default_avatar)
                        .error(R.drawable.em_default_avatar)
                        .centerCrop();
                Glide.with(context).load(user.getAvatar()).apply(options).into(imageView);
            } catch (Exception e) {
                //use default avatar
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.em_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(context).load(user.getAvatar()).apply(options).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.em_default_avatar).into(imageView);
        }
    }

    static public void getNameAndHeadPost(final String id,final TextView nick) {
        final List<OkHttpUtils.Param> list = new ArrayList<OkHttpUtils.Param>();
        final String url="http://wyusig.com:8080/starry/user/findUserById";
        //可以传多个参数，这里只写传一个参数，需要传多个参数时list.add();
        OkHttpUtils.Param idParam = new OkHttpUtils.Param("id", id);
        list.add(idParam);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //post方式连接  url，post方式请求必须传参
                //参数方式：OkHttpUtils.post(url,OkHttpUtils.ResultCallback(),list)
                OkHttpUtils.post(url, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        Log.d("testRun", "response------b" + response.toString());
                        try {// 不要在这个try catch里对ResultDto进行调用，因为这里解析json数据可能会因为后台出错等各种问题导致解析结果异常
                            // 解析后台传过来的json数据时，ResultDto类里Object要改为对应的实体,例如User或者List<User>
                            resultDto = OkHttpUtils.getObjectFromJson(response.toString(), UserResultDto.class);
                        } catch (Exception e) {
                            //json数据解析出错，可能是后台传过来的数据有问题，有可能是ResultDto实体相应的参数没对应上，客户端出错
                            resultDto = UserResultDto.error("Exception:"+e.getClass());
                            e.printStackTrace();
                            //Toast.makeText(getContext(),"服务器出错了",Toast.LENGTH_SHORT).show();
                            Log.e("wnf", "Exception------" + e.getMessage());
                        }
                        if(resultDto.getData()!=null){
                            String name=resultDto.getData().getName();
                            String head="http://wyusig.com:8080/starry/img/"+resultDto.getData().getHeadimage();
                            nick.setText(name);
                        }else {

                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("testRun", "请求失败------Exception:"+e.getMessage());
//                        Toast.makeText(getContext(), "网络请求失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                }, list);
            }

        }).start();

    }
    static public void getNameAndHeadPost(final String id,final ImageView avatar,final Context context) {
        final List<OkHttpUtils.Param> list = new ArrayList<OkHttpUtils.Param>();
        final String url="http://wyusig.com:8080/starry/user/findUserById";
        //可以传多个参数，这里只写传一个参数，需要传多个参数时list.add();
        OkHttpUtils.Param idParam = new OkHttpUtils.Param("id", id);
        list.add(idParam);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //post方式连接  url，post方式请求必须传参
                //参数方式：OkHttpUtils.post(url,OkHttpUtils.ResultCallback(),list)
                OkHttpUtils.post(url, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        Log.d("testRun", "response------b" + response.toString());
                        try {// 不要在这个try catch里对ResultDto进行调用，因为这里解析json数据可能会因为后台出错等各种问题导致解析结果异常
                            // 解析后台传过来的json数据时，ResultDto类里Object要改为对应的实体,例如User或者List<User>
                            resultDto = OkHttpUtils.getObjectFromJson(response.toString(), UserResultDto.class);
                        } catch (Exception e) {
                            //json数据解析出错，可能是后台传过来的数据有问题，有可能是ResultDto实体相应的参数没对应上，客户端出错
                            resultDto = UserResultDto.error("Exception:"+e.getClass());
                            e.printStackTrace();
                            //Toast.makeText(getContext(),"服务器出错了",Toast.LENGTH_SHORT).show();
                            Log.e("wnf", "Exception------" + e.getMessage());
                        }
                        if(resultDto.getData()!=null){
                            String name=resultDto.getData().getName();
                            String head="http://wyusig.com:8080/starry/img/"+resultDto.getData().getHeadimage();
                            RequestOptions options = new RequestOptions()
                                    .placeholder(R.drawable.em_default_avatar)
                                    .error(R.drawable.em_default_avatar)
                                    .centerCrop();
                            Glide.with(context).load(head).apply(options).into(avatar);
                        }else {

                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("testRun", "请求失败------Exception:"+e.getMessage());
//                        Toast.makeText(getContext(), "网络请求失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                }, list);
            }

        }).start();

    }
}
