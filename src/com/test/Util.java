package com.test;
import java.net.*;
import java.io.*;
import java.sql.Connection
;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.*;
import java.util.*;
import javax.net.ssl.*;
import org.json.*;

public class Util
{
	
	
	
	public static String http_dns(String host)
	{  
		try
		{  
			URL lll = new URL("http://119.29.29.29/d?dn="+host);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			String hosts = sb.toString();
			if(hosts.contains(";")){
				return hosts.split(";")[0];
			}else{
				return hosts;
			}
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
	public static void log(String string){
		SimpleDateFormat format0 = new SimpleDateFormat("[HH:mm:ss]");
		format0.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String log = format0.format(new Date().getTime());
		System.out.println(log+" "+string);
	}
	
	
	
	public static String Auth_Curl(String id,SqlListener listener,String url, String vg_api_key) 
	{

		try
		{  
			URL lll = new URL(url);    //把字符串转换为URL请求地址  
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + vg_api_key);
			connection.setRequestProperty("Accept", "application/vnd.api+json");
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流  
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch(FileNotFoundException e){
			if(id!=null){
			    listener.call("delete from Players where player_id = \""+id+"\";");
			}
			Util.log(e.getMessage());
		}
		catch (Exception e)
		{  
		    Util.log("错误-----------------"+e.getMessage()+vg_api_key.substring(vg_api_key.lastIndexOf(".")+1));
		}
		return null;
	}  

	
	
	public static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session)
		{
            return true;
        }
    };

	public static void trustAllHttpsCertificates() throws Exception
	{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
			.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
																	.getSocketFactory());
	}

	public static class miTM implements javax.net.ssl.TrustManager,
	javax.net.ssl.X509TrustManager
	{
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		public static boolean isServerTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public static boolean isClientTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public void checkServerTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}

		public void checkClientTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}
	}
	
}
