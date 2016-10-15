package ftp.core.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Kosta_Chuturkov on 10/13/2016.
 */
public class HttpRequestParameters {
    final HttpServletRequest request;
    final HttpServletResponse response;

    final MultipartFile file;

    public HttpRequestParameters(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {
        this.request = request;
        this.response = response;
        this.file = file;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public MultipartFile getFile() {
        return this.file;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }
}
