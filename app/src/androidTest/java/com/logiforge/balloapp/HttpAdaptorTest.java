package com.logiforge.balloapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by iorlanov on 8/18/17.
 */
@RunWith(Parameterized.class)
public class HttpAdaptorTest {
    private static final String REQ_PAR_OP = "op";
    private static final String REQ_PAR_VALUE = "value";
    private static final String REQ_PAR_BIN_VALUE = "bin_value";

    private static final String OP_PLAIN_TEXT = "plain_text";
    private static final String OP_BINARY = "binary";
    private static final String OP_SET_SESSION_VALUE = "set_session_value";
    private static final String OP_GET_SESSION_VALUE = "get_session_value";

    private static final String TEXT_VALUE = "Aa 日本語のキーボード";

    //private static final String TEST_URL = "http://10.0.0.21:8080/test";
    private static final String TEST_URL = "https://ballo-test.appspot.com/test";

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"com.logiforge.ballo.net.httpclient.HttpClientAdaptorBuilder"},
                {"com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder"}
        });
    }

    private HttpAdaptor httpAdaptor;
    private HttpAdaptor httpAdaptorWithCookies;

    public HttpAdaptorTest(String adaptorClassName) throws Exception{
        httpAdaptor = createAdaptor(adaptorClassName, false);
        httpAdaptorWithCookies = createAdaptor(adaptorClassName, true);
    }

    @Test
    public void contextTest() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.logiforge.balloapp", appContext.getPackageName());
    }

    @Test
    public void cookieTest() throws Exception {
        PostRequest postRequest = new PostRequest(TEST_URL, 1);
        postRequest.addStringPart(REQ_PAR_OP, OP_SET_SESSION_VALUE);
        postRequest.addStringPart(REQ_PAR_VALUE, TEXT_VALUE);
        httpAdaptorWithCookies.execute(postRequest);

        postRequest = new PostRequest(TEST_URL, 1);
        postRequest.addStringPart(REQ_PAR_OP, OP_GET_SESSION_VALUE);
        Response response = httpAdaptorWithCookies.execute(postRequest);

        assertEquals(response.getStringResponse(), TEXT_VALUE);
    }

    @Test
    public void textPost() throws Exception {
        PostRequest postRequest = new PostRequest(TEST_URL, 1);
        postRequest.addStringPart(REQ_PAR_OP, OP_PLAIN_TEXT);
        postRequest.addStringPart(REQ_PAR_VALUE, TEXT_VALUE);

        Response response = httpAdaptor.execute(postRequest);

        assertEquals(response.getStringResponse(), TEXT_VALUE);
    }

    @Test
    public void multipartPost() throws Exception {
        PostRequest postRequest = new PostRequest(TEST_URL, 1);
        postRequest.addStringPart(REQ_PAR_OP, OP_BINARY);
        postRequest.addStringPart(REQ_PAR_VALUE, TEXT_VALUE);
        postRequest.addBinaryPart(REQ_PAR_BIN_VALUE, TEXT_VALUE.getBytes());

        Response response = httpAdaptor.execute(postRequest);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int n = -1;
        while((n = response.getBinaryResponse().read(buffer, 0, 2048)) != -1) {
            content.write(buffer, 0, n);
        }
        response.getBinaryResponse().close();

        assertArrayEquals(content.toByteArray(), TEXT_VALUE.getBytes());
    }

    private HttpAdaptor createAdaptor(String adaptorBuilderClassName, boolean useCookies) throws Exception {
        Class<?> adaptorClass = Class.forName(adaptorBuilderClassName);
        Constructor<?> ctor = adaptorClass.getConstructor();
        HttpAdaptorBuilder adaptorBuilder =  (HttpAdaptorBuilder)ctor.newInstance();
        if(useCookies) {
            adaptorBuilder.useCookies();
        }
        return adaptorBuilder.build();
    }
}
