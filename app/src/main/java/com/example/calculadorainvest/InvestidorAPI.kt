import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InvestidorApi {
    @GET("acoes/{codigo}")
    fun getAcao(@Path("codigo") codigo: String): Call<AcaoResponse>
}

data class AcaoResponse(
    val valorCotacao: Double,
    val dividendos: List<Dividendo>
)

data class Dividendo(
    val dataPagamento: String,
    val valor: Double
)
