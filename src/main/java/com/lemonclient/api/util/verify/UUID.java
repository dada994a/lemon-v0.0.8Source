package com.lemonclient.api.util.verify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.Proxy.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UUID {
   public static final String CHARSET_UTF8 = "UTF-8";
   public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
   public static final String CONTENT_TYPE_JSON = "application/json";
   public static final String ENCODING_GZIP = "gzip";
   public static final String HEADER_ACCEPT = "Accept";
   public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
   public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
   public static final String HEADER_AUTHORIZATION = "Authorization";
   public static final String HEADER_CACHE_CONTROL = "Cache-Control";
   public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
   public static final String HEADER_CONTENT_LENGTH = "Content-Length";
   public static final String HEADER_CONTENT_TYPE = "Content-Type";
   public static final String HEADER_DATE = "Date";
   public static final String HEADER_ETAG = "ETag";
   public static final String HEADER_EXPIRES = "Expires";
   public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
   public static final String HEADER_LAST_MODIFIED = "Last-Modified";
   public static final String HEADER_LOCATION = "Location";
   public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
   public static final String HEADER_REFERER = "Referer";
   public static final String HEADER_SERVER = "Server";
   public static final String HEADER_USER_AGENT = "User-Agent";
   public static final String METHOD_POST = "POST";
   public static final String PARAM_CHARSET = "charset";
   private static final String BOUNDARY = "00content0boundary00";
   private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=00content0boundary00";
   private static final String CRLF = "\r\n";
   private static final String[] EMPTY_STRINGS = new String[0];
   private static SSLSocketFactory TRUSTED_FACTORY;
   private static HostnameVerifier TRUSTED_VERIFIER;
   private static final UUID.ConnectionFactory CONNECTION_FACTORY;
   private HttpURLConnection connection = null;
   private final URL url;
   private final String requestMethod;
   private UUID.RequestOutputStream output;
   private boolean multipart;
   private boolean form;
   private boolean ignoreCloseExceptions = true;
   private boolean uncompress = false;
   private int bufferSize = 8192;
   private long totalSize = -1L;
   private long totalWritten = 0L;
   private String httpProxyHost;
   private int httpProxyPort;
   private UUID.UploadProgress progress;

   private static String getValidCharset(String charset) {
      return charset != null && charset.length() > 0 ? charset : "UTF-8";
   }

   private static SSLSocketFactory getTrustedFactory() throws UUID.HttpRequestException {
      if (TRUSTED_FACTORY == null) {
         TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
         }};

         try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            TRUSTED_FACTORY = context.getSocketFactory();
         } catch (GeneralSecurityException var3) {
            IOException ioException = new IOException("Security exception configuring SSL context", var3);
            throw new UUID.HttpRequestException(ioException);
         }
      }

      return TRUSTED_FACTORY;
   }

   private static HostnameVerifier getTrustedVerifier() {
      if (TRUSTED_VERIFIER == null) {
         TRUSTED_VERIFIER = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
               return true;
            }
         };
      }

      return TRUSTED_VERIFIER;
   }

   private static StringBuilder addPathSeparator(String baseUrl, StringBuilder result) {
      if (baseUrl.indexOf(58) + 2 == baseUrl.lastIndexOf(47)) {
         result.append('/');
      }

      return result;
   }

   private static StringBuilder addParamPrefix(String baseUrl, StringBuilder result) {
      int queryStart = baseUrl.indexOf(63);
      int lastChar = result.length() - 1;
      if (queryStart == -1) {
         result.append('?');
      } else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&') {
         result.append('&');
      }

      return result;
   }

   private static StringBuilder addParam(Object key, Object value, StringBuilder result) {
      if (value != null && value.getClass().isArray()) {
         value = arrayToList(value);
      }

      if (value instanceof Iterable) {
         Iterator iterator = ((Iterable)value).iterator();

         while(iterator.hasNext()) {
            result.append(key);
            result.append("[]=");
            Object element = iterator.next();
            if (element != null) {
               result.append(element);
            }

            if (iterator.hasNext()) {
               result.append("&");
            }
         }
      } else {
         result.append(key);
         result.append("=");
         if (value != null) {
            result.append(value);
         }
      }

      return result;
   }

   private static List<Object> arrayToList(Object array) {
      if (array instanceof Object[]) {
         return Arrays.asList((Object[])((Object[])array));
      } else {
         List<Object> result = new ArrayList();
         int var3;
         int var4;
         if (array instanceof int[]) {
            int[] var2 = (int[])((int[])array);
            var3 = var2.length;

            for(var4 = 0; var4 < var3; ++var4) {
               int value = var2[var4];
               result.add(value);
            }
         } else if (array instanceof boolean[]) {
            boolean[] var8 = (boolean[])((boolean[])array);
            var3 = var8.length;

            for(var4 = 0; var4 < var3; ++var4) {
               boolean value = var8[var4];
               result.add(value);
            }
         } else if (array instanceof long[]) {
            long[] var9 = (long[])((long[])array);
            var3 = var9.length;

            for(var4 = 0; var4 < var3; ++var4) {
               long value = var9[var4];
               result.add(value);
            }
         } else if (array instanceof float[]) {
            float[] var10 = (float[])((float[])array);
            var3 = var10.length;

            for(var4 = 0; var4 < var3; ++var4) {
               float value = var10[var4];
               result.add(value);
            }
         } else if (array instanceof double[]) {
            double[] var11 = (double[])((double[])array);
            var3 = var11.length;

            for(var4 = 0; var4 < var3; ++var4) {
               double value = var11[var4];
               result.add(value);
            }
         } else if (array instanceof short[]) {
            short[] var12 = (short[])((short[])array);
            var3 = var12.length;

            for(var4 = 0; var4 < var3; ++var4) {
               short value = var12[var4];
               result.add(value);
            }
         } else if (array instanceof byte[]) {
            byte[] var13 = (byte[])((byte[])array);
            var3 = var13.length;

            for(var4 = 0; var4 < var3; ++var4) {
               byte value = var13[var4];
               result.add(value);
            }
         } else if (array instanceof char[]) {
            char[] var14 = (char[])((char[])array);
            var3 = var14.length;

            for(var4 = 0; var4 < var3; ++var4) {
               char value = var14[var4];
               result.add(value);
            }
         }

         return result;
      }
   }

   public static String encode(CharSequence url) throws UUID.HttpRequestException {
      URL parsed;
      try {
         parsed = new URL(url.toString());
      } catch (IOException var7) {
         throw new UUID.HttpRequestException(var7);
      }

      String host = parsed.getHost();
      int port = parsed.getPort();
      if (port != -1) {
         host = host + ':' + port;
      }

      try {
         String encoded = (new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), (String)null)).toASCIIString();
         int paramsStart = encoded.indexOf(63);
         if (paramsStart > 0 && paramsStart + 1 < encoded.length()) {
            encoded = encoded.substring(0, paramsStart + 1) + encoded.substring(paramsStart + 1).replace("+", "%2B");
         }

         return encoded;
      } catch (URISyntaxException var6) {
         IOException io = new IOException("Parsing URI failed", var6);
         throw new UUID.HttpRequestException(io);
      }
   }

   public static String append(CharSequence url, Map<?, ?> params) {
      String baseUrl = url.toString();
      if (params != null && !params.isEmpty()) {
         StringBuilder result = new StringBuilder(baseUrl);
         addPathSeparator(baseUrl, result);
         addParamPrefix(baseUrl, result);
         Iterator<?> iterator = params.entrySet().iterator();
         Entry<?, ?> entry = (Entry)iterator.next();
         addParam(entry.getKey().toString(), entry.getValue(), result);

         while(iterator.hasNext()) {
            result.append('&');
            entry = (Entry)iterator.next();
            addParam(entry.getKey().toString(), entry.getValue(), result);
         }

         return result.toString();
      } else {
         return baseUrl;
      }
   }

   public static String append(CharSequence url, Object... params) {
      String baseUrl = url.toString();
      if (params != null && params.length != 0) {
         if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Must specify an even number of parameter names/values");
         } else {
            StringBuilder result = new StringBuilder(baseUrl);
            addPathSeparator(baseUrl, result);
            addParamPrefix(baseUrl, result);
            addParam(params[0], params[1], result);

            for(int i = 2; i < params.length; i += 2) {
               result.append('&');
               addParam(params[i], params[i + 1], result);
            }

            return result.toString();
         }
      } else {
         return baseUrl;
      }
   }

   public static UUID post(CharSequence url) throws UUID.HttpRequestException {
      return new UUID(url, "POST");
   }

   public UUID(CharSequence url, String method) throws UUID.HttpRequestException {
      this.progress = UUID.UploadProgress.DEFAULT;

      try {
         this.url = new URL(url.toString());
      } catch (MalformedURLException var4) {
         throw new UUID.HttpRequestException(var4);
      }

      this.requestMethod = method;
   }

   public UUID(URL url, String method) throws UUID.HttpRequestException {
      this.progress = UUID.UploadProgress.DEFAULT;
      this.url = url;
      this.requestMethod = method;
   }

   private Proxy createProxy() {
      return new Proxy(Type.HTTP, new InetSocketAddress(this.httpProxyHost, this.httpProxyPort));
   }

   private HttpURLConnection createConnection() {
      try {
         HttpURLConnection connection;
         if (this.httpProxyHost != null) {
            connection = CONNECTION_FACTORY.create(this.url, this.createProxy());
         } else {
            connection = CONNECTION_FACTORY.create(this.url);
         }

         connection.setRequestMethod(this.requestMethod);
         return connection;
      } catch (IOException var2) {
         throw new UUID.HttpRequestException(var2);
      }
   }

   public String toString() {
      return this.method() + ' ' + this.url();
   }

   public HttpURLConnection getConnection() {
      if (this.connection == null) {
         this.connection = this.createConnection();
      }

      return this.connection;
   }

   public UUID ignoreCloseExceptions(boolean ignore) {
      this.ignoreCloseExceptions = ignore;
      return this;
   }

   public boolean ignoreCloseExceptions() {
      return this.ignoreCloseExceptions;
   }

   public int code() throws UUID.HttpRequestException {
      try {
         this.closeOutput();
         return this.getConnection().getResponseCode();
      } catch (IOException var2) {
         throw new UUID.HttpRequestException(var2);
      }
   }

   public UUID code(AtomicInteger output) throws UUID.HttpRequestException {
      output.set(this.code());
      return this;
   }

   public boolean ok() throws UUID.HttpRequestException {
      return 200 == this.code();
   }

   public boolean created() throws UUID.HttpRequestException {
      return 201 == this.code();
   }

   public boolean noContent() throws UUID.HttpRequestException {
      return 204 == this.code();
   }

   public boolean serverError() throws UUID.HttpRequestException {
      return 500 == this.code();
   }

   public boolean badRequest() throws UUID.HttpRequestException {
      return 400 == this.code();
   }

   public boolean notFound() throws UUID.HttpRequestException {
      return 404 == this.code();
   }

   public boolean notModified() throws UUID.HttpRequestException {
      return 304 == this.code();
   }

   public String message() throws UUID.HttpRequestException {
      try {
         this.closeOutput();
         return this.getConnection().getResponseMessage();
      } catch (IOException var2) {
         throw new UUID.HttpRequestException(var2);
      }
   }

   public UUID disconnect() {
      this.getConnection().disconnect();
      return this;
   }

   public UUID chunk(int size) {
      this.getConnection().setChunkedStreamingMode(size);
      return this;
   }

   public UUID bufferSize(int size) {
      if (size < 1) {
         throw new IllegalArgumentException("Size must be greater than zero");
      } else {
         this.bufferSize = size;
         return this;
      }
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   public UUID uncompress(boolean uncompress) {
      this.uncompress = uncompress;
      return this;
   }

   protected ByteArrayOutputStream byteStream() {
      int size = this.contentLength();
      return size > 0 ? new ByteArrayOutputStream(size) : new ByteArrayOutputStream();
   }

   public String body(String charset) throws UUID.HttpRequestException {
      ByteArrayOutputStream output = this.byteStream();

      try {
         this.copy((InputStream)this.buffer(), (OutputStream)output);
         return output.toString(getValidCharset(charset));
      } catch (IOException var4) {
         throw new UUID.HttpRequestException(var4);
      }
   }

   public String body() throws UUID.HttpRequestException {
      return this.body(this.charset());
   }

   public BufferedInputStream buffer() throws UUID.HttpRequestException {
      return new BufferedInputStream(this.stream(), this.bufferSize);
   }

   public InputStream stream() throws UUID.HttpRequestException {
      Object stream;
      if (this.code() < 400) {
         try {
            stream = this.getConnection().getInputStream();
         } catch (IOException var4) {
            throw new UUID.HttpRequestException(var4);
         }
      } else {
         stream = this.getConnection().getErrorStream();
         if (stream == null) {
            try {
               stream = this.getConnection().getInputStream();
            } catch (IOException var5) {
               if (this.contentLength() > 0) {
                  throw new UUID.HttpRequestException(var5);
               }

               stream = new ByteArrayInputStream(new byte[0]);
            }
         }
      }

      if (this.uncompress && "gzip".equals(this.contentEncoding())) {
         try {
            return new GZIPInputStream((InputStream)stream);
         } catch (IOException var3) {
            throw new UUID.HttpRequestException(var3);
         }
      } else {
         return (InputStream)stream;
      }
   }

   public InputStreamReader reader(String charset) throws UUID.HttpRequestException {
      try {
         return new InputStreamReader(this.stream(), getValidCharset(charset));
      } catch (UnsupportedEncodingException var3) {
         throw new UUID.HttpRequestException(var3);
      }
   }

   public BufferedReader bufferedReader(String charset) throws UUID.HttpRequestException {
      return new BufferedReader(this.reader(charset), this.bufferSize);
   }

   public UUID receive(OutputStream output) throws UUID.HttpRequestException {
      try {
         return this.copy((InputStream)this.buffer(), (OutputStream)output);
      } catch (IOException var3) {
         throw new UUID.HttpRequestException(var3);
      }
   }

   public UUID header(String name, String value) {
      this.getConnection().setRequestProperty(name, value);
      return this;
   }

   public String header(String name) throws UUID.HttpRequestException {
      this.closeOutputQuietly();
      return this.getConnection().getHeaderField(name);
   }

   public int intHeader(String name) throws UUID.HttpRequestException {
      return this.intHeader(name, -1);
   }

   public int intHeader(String name, int defaultValue) throws UUID.HttpRequestException {
      this.closeOutputQuietly();
      return this.getConnection().getHeaderFieldInt(name, defaultValue);
   }

   public String parameter(String headerName, String paramName) {
      return this.getParam(this.header(headerName), paramName);
   }

   protected String getParam(String value, String paramName) {
      if (value != null && value.length() != 0) {
         int length = value.length();
         int start = value.indexOf(59) + 1;
         if (start != 0 && start != length) {
            int end = value.indexOf(59, start);
            if (end == -1) {
               end = length;
            }

            while(start < end) {
               int nameEnd = value.indexOf(61, start);
               if (nameEnd != -1 && nameEnd < end && paramName.equals(value.substring(start, nameEnd).trim())) {
                  String paramValue = value.substring(nameEnd + 1, end).trim();
                  int valueLength = paramValue.length();
                  if (valueLength != 0) {
                     if (valueLength > 2 && '"' == paramValue.charAt(0) && '"' == paramValue.charAt(valueLength - 1)) {
                        return paramValue.substring(1, valueLength - 1);
                     }

                     return paramValue;
                  }
               }

               start = end + 1;
               end = value.indexOf(59, start);
               if (end == -1) {
                  end = length;
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public String charset() {
      return this.parameter("Content-Type", "charset");
   }

   public UUID acceptEncoding(String acceptEncoding) {
      return this.header("Accept-Encoding", acceptEncoding);
   }

   public String contentEncoding() {
      return this.header("Content-Encoding");
   }

   public UUID authorization(String authorization) {
      return this.header("Authorization", authorization);
   }

   public UUID proxyAuthorization(String proxyAuthorization) {
      return this.header("Proxy-Authorization", proxyAuthorization);
   }

   public UUID contentType(String contentType) {
      return this.contentType(contentType, (String)null);
   }

   public UUID contentType(String contentType, String charset) {
      if (charset != null && charset.length() > 0) {
         String separator = "; charset=";
         return this.header("Content-Type", contentType + "; charset=" + charset);
      } else {
         return this.header("Content-Type", contentType);
      }
   }

   public int contentLength() {
      return this.intHeader("Content-Length");
   }

   public UUID contentLength(int contentLength) {
      this.getConnection().setFixedLengthStreamingMode(contentLength);
      return this;
   }

   public UUID accept(String accept) {
      return this.header("Accept", accept);
   }

   public UUID acceptJson() {
      return this.accept("application/json");
   }

   protected UUID copy(final InputStream input, final OutputStream output) throws IOException {
      return (UUID)(new UUID.CloseOperation<UUID>(input, this.ignoreCloseExceptions) {
         public UUID run() throws IOException {
            byte[] buffer = new byte[UUID.this.bufferSize];

            int read;
            while((read = input.read(buffer)) != -1) {
               output.write(buffer, 0, read);
               UUID.this.totalWritten = UUID.this.totalWritten + (long)read;
               UUID.this.progress.onUpload(UUID.this.totalWritten, UUID.this.totalSize);
            }

            return UUID.this;
         }
      }).call();
   }

   protected UUID copy(final Reader input, final Writer output) throws IOException {
      return (UUID)(new UUID.CloseOperation<UUID>(input, this.ignoreCloseExceptions) {
         public UUID run() throws IOException {
            char[] buffer = new char[UUID.this.bufferSize];

            int read;
            while((read = input.read(buffer)) != -1) {
               output.write(buffer, 0, read);
               UUID.this.totalWritten = UUID.this.totalWritten + (long)read;
               UUID.this.progress.onUpload(UUID.this.totalWritten, -1L);
            }

            return UUID.this;
         }
      }).call();
   }

   public UUID progress(UUID.UploadProgress callback) {
      if (callback == null) {
         this.progress = UUID.UploadProgress.DEFAULT;
      } else {
         this.progress = callback;
      }

      return this;
   }

   private UUID incrementTotalSize(long size) {
      if (this.totalSize == -1L) {
         this.totalSize = 0L;
      }

      this.totalSize += size;
      return this;
   }

   protected UUID closeOutput() throws IOException {
      this.progress((UUID.UploadProgress)null);
      if (this.output == null) {
         return this;
      } else {
         if (this.multipart) {
            this.output.write("\r\n--00content0boundary00--\r\n");
         }

         if (this.ignoreCloseExceptions) {
            try {
               this.output.close();
            } catch (IOException var2) {
            }
         } else {
            this.output.close();
         }

         this.output = null;
         return this;
      }
   }

   protected UUID closeOutputQuietly() throws UUID.HttpRequestException {
      try {
         return this.closeOutput();
      } catch (IOException var2) {
         throw new UUID.HttpRequestException(var2);
      }
   }

   protected UUID openOutput() throws IOException {
      if (this.output != null) {
         return this;
      } else {
         this.getConnection().setDoOutput(true);
         String charset = this.getParam(this.getConnection().getRequestProperty("Content-Type"), "charset");
         this.output = new UUID.RequestOutputStream(this.getConnection().getOutputStream(), charset, this.bufferSize);
         return this;
      }
   }

   protected UUID startPart() throws IOException {
      if (!this.multipart) {
         this.multipart = true;
         this.contentType("multipart/form-data; boundary=00content0boundary00").openOutput();
         this.output.write("--00content0boundary00\r\n");
      } else {
         this.output.write("\r\n--00content0boundary00\r\n");
      }

      return this;
   }

   protected UUID writePartHeader(String name, String filename, String contentType) throws IOException {
      StringBuilder partBuffer = new StringBuilder();
      partBuffer.append("form-data; name=\"").append(name);
      if (filename != null) {
         partBuffer.append("\"; filename=\"").append(filename);
      }

      partBuffer.append('"');
      this.partHeader("Content-Disposition", partBuffer.toString());
      if (contentType != null) {
         this.partHeader("Content-Type", contentType);
      }

      return this.send((CharSequence)"\r\n");
   }

   public UUID part(String name, String filename, String part) throws UUID.HttpRequestException {
      return this.part(name, filename, (String)null, (String)part);
   }

   public UUID part(String name, String filename, String contentType, String part) throws UUID.HttpRequestException {
      try {
         this.startPart();
         this.writePartHeader(name, filename, contentType);
         this.output.write(part);
         return this;
      } catch (IOException var6) {
         throw new UUID.HttpRequestException(var6);
      }
   }

   public UUID part(String name, String filename, Number part) throws UUID.HttpRequestException {
      return this.part(name, filename, part != null ? part.toString() : null);
   }

   public UUID part(String name, String filename, File part) throws UUID.HttpRequestException {
      return this.part(name, filename, (String)null, (File)part);
   }

   public UUID part(String name, String filename, String contentType, File part) throws UUID.HttpRequestException {
      BufferedInputStream stream;
      try {
         stream = new BufferedInputStream(new FileInputStream(part));
         this.incrementTotalSize(part.length());
      } catch (IOException var7) {
         throw new UUID.HttpRequestException(var7);
      }

      return this.part(name, filename, contentType, (InputStream)stream);
   }

   public UUID part(String name, String filename, String contentType, InputStream part) throws UUID.HttpRequestException {
      try {
         this.startPart();
         this.writePartHeader(name, filename, contentType);
         this.copy((InputStream)part, (OutputStream)this.output);
         return this;
      } catch (IOException var6) {
         throw new UUID.HttpRequestException(var6);
      }
   }

   public UUID partHeader(String name, String value) throws UUID.HttpRequestException {
      return this.send((CharSequence)name).send((CharSequence)": ").send((CharSequence)value).send((CharSequence)"\r\n");
   }

   public UUID send(InputStream input) throws UUID.HttpRequestException {
      try {
         this.openOutput();
         this.copy((InputStream)input, (OutputStream)this.output);
         return this;
      } catch (IOException var3) {
         throw new UUID.HttpRequestException(var3);
      }
   }

   public UUID send(CharSequence value) throws UUID.HttpRequestException {
      try {
         this.openOutput();
         this.output.write(value.toString());
         return this;
      } catch (IOException var3) {
         throw new UUID.HttpRequestException(var3);
      }
   }

   public UUID form(Entry<?, ?> entry, String charset) throws UUID.HttpRequestException {
      return this.form(entry.getKey(), entry.getValue(), charset);
   }

   public UUID form(Object name, Object value, String charset) throws UUID.HttpRequestException {
      boolean first = !this.form;
      if (first) {
         this.contentType("application/x-www-form-urlencoded", charset);
         this.form = true;
      }

      charset = getValidCharset(charset);

      try {
         this.openOutput();
         if (!first) {
            this.output.write(38);
         }

         this.output.write(URLEncoder.encode(name.toString(), charset));
         this.output.write(61);
         if (value != null) {
            this.output.write(URLEncoder.encode(value.toString(), charset));
         }

         return this;
      } catch (IOException var6) {
         throw new UUID.HttpRequestException(var6);
      }
   }

   public UUID form(Map<?, ?> values, String charset) throws UUID.HttpRequestException {
      if (!values.isEmpty()) {
         Iterator var3 = values.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<?, ?> entry = (Entry)var3.next();
            this.form(entry, charset);
         }
      }

      return this;
   }

   public URL url() {
      return this.getConnection().getURL();
   }

   public String method() {
      return this.getConnection().getRequestMethod();
   }

   static {
      CONNECTION_FACTORY = UUID.ConnectionFactory.DEFAULT;
   }

   public static class RequestOutputStream extends BufferedOutputStream {
      private final CharsetEncoder encoder;

      public RequestOutputStream(OutputStream stream, String charset, int bufferSize) {
         super(stream, bufferSize);
         this.encoder = Charset.forName(UUID.getValidCharset(charset)).newEncoder();
      }

      public UUID.RequestOutputStream write(String value) throws IOException {
         ByteBuffer bytes = this.encoder.encode(CharBuffer.wrap(value));
         super.write(bytes.array(), 0, bytes.limit());
         return this;
      }
   }

   protected abstract static class FlushOperation<V> extends UUID.Operation<V> {
      private final Flushable flushable;

      protected FlushOperation(Flushable flushable) {
         this.flushable = flushable;
      }

      protected void done() throws IOException {
         this.flushable.flush();
      }
   }

   protected abstract static class CloseOperation<V> extends UUID.Operation<V> {
      private final Closeable closeable;
      private final boolean ignoreCloseExceptions;

      protected CloseOperation(Closeable closeable, boolean ignoreCloseExceptions) {
         this.closeable = closeable;
         this.ignoreCloseExceptions = ignoreCloseExceptions;
      }

      protected void done() throws IOException {
         if (this.closeable instanceof Flushable) {
            ((Flushable)this.closeable).flush();
         }

         if (this.ignoreCloseExceptions) {
            try {
               this.closeable.close();
            } catch (IOException var2) {
            }
         } else {
            this.closeable.close();
         }

      }
   }

   protected abstract static class Operation<V> implements Callable<V> {
      protected abstract V run() throws UUID.HttpRequestException, IOException;

      protected abstract void done() throws IOException;

      public V call() throws UUID.HttpRequestException {
         boolean thrown = false;

         Object var2;
         try {
            var2 = this.run();
         } catch (UUID.HttpRequestException var11) {
            thrown = true;
            throw var11;
         } catch (IOException var12) {
            thrown = true;
            throw new UUID.HttpRequestException(var12);
         } finally {
            try {
               this.done();
            } catch (IOException var13) {
               if (!thrown) {
                  throw new UUID.HttpRequestException(var13);
               }
            }

         }

         return var2;
      }
   }

   public static class HttpRequestException extends RuntimeException {
      private static final long serialVersionUID = -1170466989781746231L;

      public HttpRequestException(IOException cause) {
         super(cause);
      }

      public IOException getCause() {
         return (IOException)super.getCause();
      }
   }

   public static class Base64 {
      private static final byte EQUALS_SIGN = 61;
      private static final String PREFERRED_ENCODING = "US-ASCII";
      private static final byte[] _STANDARD_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};

      private Base64() {
      }

      private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
         byte[] ALPHABET = _STANDARD_ALPHABET;
         int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[srcOffset + 1] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[srcOffset + 2] << 24 >>> 24 : 0);
         switch(numSigBytes) {
         case 1:
            destination[destOffset] = ALPHABET[inBuff >>> 18];
            destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
            destination[destOffset + 2] = 61;
            destination[destOffset + 3] = 61;
            return destination;
         case 2:
            destination[destOffset] = ALPHABET[inBuff >>> 18];
            destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
            destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
            destination[destOffset + 3] = 61;
            return destination;
         case 3:
            destination[destOffset] = ALPHABET[inBuff >>> 18];
            destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
            destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
            destination[destOffset + 3] = ALPHABET[inBuff & 63];
            return destination;
         default:
            return destination;
         }
      }

      public static String encode(String string) {
         byte[] bytes;
         try {
            bytes = string.getBytes("US-ASCII");
         } catch (UnsupportedEncodingException var3) {
            bytes = string.getBytes();
         }

         return encodeBytes(bytes);
      }

      public static String encodeBytes(byte[] source) {
         return encodeBytes(source, 0, source.length);
      }

      public static String encodeBytes(byte[] source, int off, int len) {
         byte[] encoded = encodeBytesToBytes(source, off, len);

         try {
            return new String(encoded, "US-ASCII");
         } catch (UnsupportedEncodingException var5) {
            return new String(encoded);
         }
      }

      public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {
         if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
         } else if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
         } else if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
         } else if (off + len > source.length) {
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", off, len, source.length));
         } else {
            int encLen = len / 3 * 4 + (len % 3 > 0 ? 4 : 0);
            byte[] outBuff = new byte[encLen];
            int d = 0;
            int e = 0;

            for(int len2 = len - 2; d < len2; e += 4) {
               encode3to4(source, d + off, 3, outBuff, e);
               d += 3;
            }

            if (d < len) {
               encode3to4(source, d + off, len - d, outBuff, e);
               e += 4;
            }

            if (e <= outBuff.length - 1) {
               byte[] finalOut = new byte[e];
               System.arraycopy(outBuff, 0, finalOut, 0, e);
               return finalOut;
            } else {
               return outBuff;
            }
         }
      }
   }

   public interface UploadProgress {
      UUID.UploadProgress DEFAULT = new UUID.UploadProgress() {
         public void onUpload(long uploaded, long total) {
         }
      };

      void onUpload(long var1, long var3);
   }

   public interface ConnectionFactory {
      UUID.ConnectionFactory DEFAULT = new UUID.ConnectionFactory() {
         public HttpURLConnection create(URL url) throws IOException {
            return (HttpURLConnection)url.openConnection();
         }

         public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
            return (HttpURLConnection)url.openConnection(proxy);
         }
      };

      HttpURLConnection create(URL var1) throws IOException;

      HttpURLConnection create(URL var1, Proxy var2) throws IOException;
   }
}
