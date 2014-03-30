package de.example.exampletdd.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;

public class WeatherHTTPClient {
    private final AndroidHttpClient httpClient;

    public WeatherHTTPClient(final AndroidHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String retrieveJSONDataFromAPI(final URL url)
            throws URISyntaxException, ClientProtocolException, IOException {

        final ResponseHandler<String> handler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response)
                            throws UnsupportedEncodingException, IOException {

                if (response != null) {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        final String contentEncoding = entity
                                .getContentEncoding()
                                .getValue();
                        final ByteArrayOutputStream buffer = WeatherHTTPClient.this
                                .sortResponse(response);
                        return new String(buffer.toByteArray(), contentEncoding);
                    }

                    throw new IOException("There is no entity");
                }

                throw new IOException("There is no response");
            }
        };

        final HttpGet httpGet = new HttpGet();
        httpGet.setURI(url.toURI());

        return this.httpClient.execute(httpGet, handler);
    }

    public ByteArrayOutputStream retrieveDataFromAPI(final URL url)
            throws URISyntaxException, ClientProtocolException, IOException {
        final ResponseHandler<ByteArrayOutputStream> handler = new ResponseHandler<ByteArrayOutputStream>() {

            @Override
            public ByteArrayOutputStream handleResponse(
                    final HttpResponse response)
                            throws UnsupportedEncodingException, IOException {

                if (response != null) {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return WeatherHTTPClient.this.sortResponse(response);
                    }

                    throw new IOException("There is no entity");
                }

                throw new IOException("There is no response");
            }
        };

        final HttpGet httpGet = new HttpGet();
        httpGet.setURI(url.toURI());

        return this.httpClient.execute(httpGet, handler);
    }

    public ByteArrayOutputStream sortResponse(final HttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            final HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                try {
                    return this.readInputStream(entity.getContent());
                } finally {
                    entity.consumeContent();
                }
            }
        }

        throw new IOException("Unexpected response code: "
                + httpResponse.getStatusLine().getStatusCode());
    }

    public void close() {
        this.httpClient.close();
    }

    private ByteArrayOutputStream readInputStream (final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        final int bufferSize = 1024;
        final byte[] buffer = new byte[bufferSize];

        try {
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }finally {
            inputStream.close();
        }

        return byteBuffer;
    }
}
