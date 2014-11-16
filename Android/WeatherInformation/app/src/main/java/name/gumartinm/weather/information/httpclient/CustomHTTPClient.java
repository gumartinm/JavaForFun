/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.gumartinm.weather.information.httpclient;

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

public class CustomHTTPClient {
    private final AndroidHttpClient httpClient;

    public CustomHTTPClient(final AndroidHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String retrieveDataAsString(final URL url)
            throws URISyntaxException, ClientProtocolException, IOException {

        final ResponseHandler<String> handler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response)
                            throws UnsupportedEncodingException, IOException {

                if (response != null) {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            final ContentType contentType = ContentType.getOrDefault(entity);
                            final ByteArrayOutputStream buffer = CustomHTTPClient.this
                                    .sortResponse(response);
                            return new String(buffer.toByteArray(), contentType.getCharset());
                        } finally {
                            entity.consumeContent();
                        }
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

    public ByteArrayOutputStream retrieveRawData(final URL url)
            throws URISyntaxException, ClientProtocolException, IOException {
        final ResponseHandler<ByteArrayOutputStream> handler = new ResponseHandler<ByteArrayOutputStream>() {

            @Override
            public ByteArrayOutputStream handleResponse(
                    final HttpResponse response)
                            throws UnsupportedEncodingException, IOException {

                if (response != null) {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            return CustomHTTPClient.this.sortResponse(response);
                        } finally {
                            entity.consumeContent();
                        }
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

    public void close() {
        this.httpClient.close();
    }

    private ByteArrayOutputStream sortResponse(final HttpResponse httpResponse) throws IOException {

        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Unexpected response code: "
                    + httpResponse.getStatusLine().getStatusCode());
        }

        final HttpEntity entity = httpResponse.getEntity();
        final InputStream inputStream = entity.getContent();
        try {
            return this.readInputStream(inputStream);
        } finally {
            inputStream.close();
        }

    }

    private ByteArrayOutputStream readInputStream (final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        final int bufferSize = 1024;
        final byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer;
    }
}
