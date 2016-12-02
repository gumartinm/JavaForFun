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
package de.rxjava.tests.httpclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class CustomHTTPClient {
    private final String userAgent;

    private CustomHTTPClient(String userAgent) {
        this.userAgent = userAgent;
    }

    public String retrieveDataAsString(final URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Cache-Control", "no-cache");
            
            try (final InputStream in = new BufferedInputStream(connection.getInputStream());
            	 final ByteArrayOutputStream buffer = readInputStream(in)) {
            	
	            // No easy way of retrieving the charset from urlConnection.getContentType()
	            // Currently OpenWeatherMap returns: application/json; charset=utf-8
	            // Let's hope they will not change the content-type :/
	            return new String(buffer.toByteArray(), Charset.forName("UTF-8"));
        	}
        } finally {
            connection.disconnect();
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

    public static final CustomHTTPClient newInstance(final String userAgent) {
        return new CustomHTTPClient(userAgent);
    }
}
