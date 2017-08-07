/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Loader;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;

/**
 *
 * @author ruben This bean starts when the server starts and enables the Unirest
 * API to ignore all https certificates in its http calls
 *
 */
@Singleton
@Startup
public class HttpsClientBuilder {

    @PostConstruct
    public void init() {
        /*try {
            
            Unirest.setHttpClient(buildIgnoreSSL(false));
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "Setting http client for Unirest");
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            Logger.getLogger(HttpsClientBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    //Methode to build a dummy HttpClient that ignores all Https certificates
    public HttpClient buildIgnoreSSL(boolean addCookieStore) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();

        if (addCookieStore) {
            BasicCookieStore cookieStore = new BasicCookieStore();
            b.setDefaultCookieStore(cookieStore);
        }
        b.disableAutomaticRetries();
        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSSLContext(sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        b.setConnectionManager(connMgr);

        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);

        // finally, build the HttpClient;
        //      -- done!
        //RequestConfig.Builder requestBuilder = RequestConfig.custom();
        //requestBuilder = requestBuilder.setConnectTimeout(20000);
        //requestBuilder = requestBuilder.setConnectionRequestTimeout(20000);
        //requestBuilder = requestBuilder.
        //b.setDefaultRequestConfig(requestBuilder.build());
        return b.build();

    }
}
