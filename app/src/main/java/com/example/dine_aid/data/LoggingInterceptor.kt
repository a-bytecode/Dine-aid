import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        // Hier wird die URL zur Übeprüfung ausgegeben
        println("Requesting URL: $url")
        return chain.proceed(request)
    }
}
