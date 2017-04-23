package org.xuxiaoxiao.xiao.infrastructure;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by WuQiang on 2017/4/14.
 */

public class Internet {
    private static final String TAG = "WQ";

    private static final String API_KEY = "REPLACE_ME_WITH_A_REAL_KEY";
    private static final String BmobApplicationId = "6ac2a7be596fe87a4e38c1f86be4e55d";
    private static final String BmobRESTAPIKey = "f7c989210c1d07b96fe3450157aeccdc";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Bmob-Application-Id", BmobApplicationId);
        connection.setRequestProperty("X-Bmob-REST-API-Key", BmobRESTAPIKey);
        connection.setRequestProperty("Content-Type", "application/json");//设定 请求格式 json


        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItems() {
//    public List<GalleryItem> fetchItems() {

//        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString("https://api.bmob.cn/1/classes/test/XxipGGGV");
            Log.i(TAG, "Received JSON: " + jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
//            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
//        catch (JSONException je) {
//            Log.e(TAG, "Failed to parse JSON", je);
//        }

//        return items;
//        return null;
    }


    public void httpUrlConnPup(String name, String password) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL("https://api.bmob.cn/1/classes/test/XxipGGGV");
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
//            urlConnection.setRequestMethod("POST");//设置请求的方式
//            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型

            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("X-Bmob-Application-Id", BmobApplicationId);
            urlConnection.setRequestProperty("X-Bmob-REST-API-Key", BmobRESTAPIKey);
            urlConnection.setRequestProperty("Content-Type", "application/json");//设定 请求格式 json


            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            JSONObject json = new JSONObject();//创建json对象
            json.put("author", URLEncoder.encode(name, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
            json.put("type", URLEncoder.encode(password, "UTF-8"));//把数据put进json对象中
            String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
            //-------------使用字节流发送数据--------------
            //OutputStream out = urlConnection.getOutputStream();
            //BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
            //byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
            //bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            //bos.flush();//刷新缓冲区，发送数据
            //out.close();
            //bos.close();
            //------------字符流写入数据------------
            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(jsonstr);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                JSONObject rjson = new JSONObject(buffer.toString());

                Log.d(TAG, "rjson=" + rjson);//rjson={"json":true}
                boolean result = rjson.getBoolean("json");//从rjson对象中得到key值为"json"的数据，这里服务端返回的是一个boolean类型的数据
                if (result) {//判断结果是否正确
//                    mHandler.sendEmptyMessage(USERLOGIN_SUCCESS);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_SUCCESS");
                } else {
//                    mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

                }
            } else {
//                mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
//            mHandler.sendEmptyMessage(USERLOGIN_FAILED);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
    }

    public void httpUrlConnPost(String name, String password) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL("https://api.bmob.cn/1/classes/test");
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
//            urlConnection.setRequestMethod("POST");//设置请求的方式
//            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("X-Bmob-Application-Id", BmobApplicationId);
            urlConnection.setRequestProperty("X-Bmob-REST-API-Key", BmobRESTAPIKey);
            urlConnection.setRequestProperty("Content-Type", "application/json");//设定 请求格式 json


            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            JSONObject json = new JSONObject();//创建json对象
            json.put("author", URLEncoder.encode(name, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
            json.put("type", URLEncoder.encode(password, "UTF-8"));//把数据put进json对象中
            String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
            //-------------使用字节流发送数据--------------
            //OutputStream out = urlConnection.getOutputStream();
            //BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
            //byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
            //bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            //bos.flush();//刷新缓冲区，发送数据
            //out.close();
            //bos.close();
            //------------字符流写入数据------------
            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(jsonstr);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                JSONObject rjson = new JSONObject(buffer.toString());

                Log.d(TAG, "rjson=" + rjson);//rjson={"json":true}
                boolean result = rjson.getBoolean("json");//从rjson对象中得到key值为"json"的数据，这里服务端返回的是一个boolean类型的数据
                if (result) {//判断结果是否正确
//                    mHandler.sendEmptyMessage(USERLOGIN_SUCCESS);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_SUCCESS");
                } else {
//                    mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

                }
            } else {
//                mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
//            mHandler.sendEmptyMessage(USERLOGIN_FAILED);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
    }

    public void BmobPostFile(String name, String password) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(" https://api.bmob.cn/2/files/hello.txt");
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
//            urlConnection.setRequestMethod("POST");//设置请求的方式
//            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("X-Bmob-Application-Id", BmobApplicationId);
            urlConnection.setRequestProperty("X-Bmob-REST-API-Key", BmobRESTAPIKey);
            urlConnection.setRequestProperty("Content-Type", "text/plain");//设定 请求格式 json
//            urlConnection.setRequestProperty("Content-Type", "application/json");//设定 请求格式 json


            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            JSONObject json = new JSONObject();//创建json对象
            json.put("author", URLEncoder.encode(name, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
            json.put("type", URLEncoder.encode(password, "UTF-8"));//把数据put进json对象中
            String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
            //-------------使用字节流发送数据--------------
            //OutputStream out = urlConnection.getOutputStream();
            //BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
            //byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
            //bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            //bos.flush();//刷新缓冲区，发送数据
            //out.close();
            //bos.close();
            //------------字符流写入数据------------
            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(jsonstr);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                JSONObject rjson = new JSONObject(buffer.toString());

                Log.d(TAG, "rjson=" + rjson);//rjson={"json":true}
                boolean result = rjson.getBoolean("json");//从rjson对象中得到key值为"json"的数据，这里服务端返回的是一个boolean类型的数据
                if (result) {//判断结果是否正确
//                    mHandler.sendEmptyMessage(USERLOGIN_SUCCESS);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_SUCCESS");
                } else {
//                    mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                    Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

                }
            } else {
//                mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
//            mHandler.sendEmptyMessage(USERLOGIN_FAILED);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        // 下面是变流的方法
//        InputStream in=context.getResources().openRawResource(图片id);
    }


    public String  BmobPostPhoto(Bitmap image, String name) {
        String photoUrl = "";
        HttpURLConnection urlConnection = null;
//        Bitmap image = drawableToBitmap(drawable);
        // Hold the bite representation of the image
        // 直接二进制的 Stream 就可以发送，不需要转换成字符串
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        // Have a String representation of the image
//        String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        URL url = null;
        try {
            url = new URL(" https://api.bmob.cn/2/files/bbda.png");
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(5000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
//            urlConnection.setRequestMethod("POST");//设置请求的方式
//            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("X-Bmob-Application-Id", BmobApplicationId);
            urlConnection.setRequestProperty("X-Bmob-REST-API-Key", BmobRESTAPIKey);
            urlConnection.setRequestProperty("Content-Type", "image/jpeg");//设定 请求格式 json
//            urlConnection.setRequestProperty("Content-Type", "application/json");//设定 请求格式 json


            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
//            JSONObject json = new JSONObject();//创建json对象
//            json.put("author", URLEncoder.encode(name, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
//            json.put("type", URLEncoder.encode(password, "UTF-8"));//把数据put进json对象中
//            String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
            //-------------使用字节流发送数据--------------
            OutputStream out = urlConnection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
//            byte[] bytes = byteArrayOutputStream.toByteArray();//把字符串转化为字节数组

            bos.write(byteArrayOutputStream.toByteArray());//把这个字节数组的数据写入缓冲区中
//            bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            bos.flush();//刷新缓冲区，发送数据，这个是必须的在关闭Stream之前
            out.close();
            bos.close();

            //-------------使用字节流发送数据--------------
//            OutputStream out = urlConnection.getOutputStream();
//            BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
//            byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
//            bos.write(bytes);//把这个字节数组的数据写入缓冲区中
//            bos.flush();//刷新缓冲区，发送数据
//            out.close();
//            bos.close();

            //------------字符流写入数据------------
//            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
//            bw.write(jsonstr);//把json字符串写入缓冲区中
//            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
//            out.close();
//            bw.close();//使用完关闭

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                Log.d("WQWQ", buffer.toString());
                JSONObject json = new JSONObject(buffer.toString());
                Log.d("WQWQ",json.getString("url"));
                photoUrl = json.getString("url");

            } else {
//                mHandler.sendEmptyMessage(USERLOGIN_FAILED);
                Log.e(TAG, "Failed to fetch items" + "USERLOGIN_FAILED");

            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
//            mHandler.sendEmptyMessage(USERLOGIN_FAILED);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        // 下面是变流的方法
//        InputStream in=context.getResources().openRawResource(图片id);
        return photoUrl;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int width = drawable.getIntrinsicWidth();

        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height,

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);

        drawable.draw(canvas);

        return bitmap;


    }
}
