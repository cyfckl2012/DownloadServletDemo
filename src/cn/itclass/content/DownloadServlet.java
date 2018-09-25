package cn.itclass.content;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

/**
 * 
 * @author Damien
 * @date 2018年9月25日
 * @parameter 	功能：下载文件 
 * 				备注：有防止乱码的功能
 */
public class DownloadServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 1、获得要下载的文件的名称
		String filename = request.getParameter("filename");

		// 2、解决获得中文参数的代码
		filename = new String(filename.getBytes("ISO8859-1"), "UTF-8");

		// 3、获得请求头中的User-Agent
		String agent = request.getHeader("User-Agent");

		// 4、根据不同浏览器进行不同的编码（第四步为模板代码，非所有浏览器适用）
		String filenameEncoder = "";
		if (agent.contains("MSIE")) {
			// IE浏览器
			filenameEncoder = URLEncoder.encode(filename, "utf-8");
			filenameEncoder = filenameEncoder.replace("+", " ");
		} else if (agent.contains("Firefox")) {
			// 火狐浏览器
			BASE64Encoder base64Encoder = new BASE64Encoder();
			filenameEncoder = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
		} else {
			// 其它浏览器
			filenameEncoder = URLEncoder.encode(filename, "utf-8");
		}

		// 5、要下载的这个文件的类型------客户端通过文件的MIME类型区别
		response.setContentType(this.getServletContext().getMimeType(filename));

		// 6、告诉客户端，改文件不是直接解析，而是以附件形式打开（下载）
		// 此行能够设置下载时默认的文件名字
		response.setHeader("Content-Disposition", "attachment;filename=" + filenameEncoder);

		// 7、获得filename的绝对地址
		String realPath = this.getServletContext().getRealPath("download/" + filename);

		// 8、获取该文件的输入流
		InputStream inputStream = new FileInputStream(realPath);

		// 9、获得输出流，通过response获得的输出流，用于向客户端写内容
		ServletOutputStream outputStream = response.getOutputStream();

		// 10、文件拷贝的模板代码
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}

		// 11、关闭流
		inputStream.close();
		outputStream.close();// 一般不写，服务器会自动帮你关上
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}