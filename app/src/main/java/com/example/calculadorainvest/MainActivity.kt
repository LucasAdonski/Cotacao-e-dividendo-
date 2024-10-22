package com.example.calculadorainvest

import AcaoResponse
import Dividendo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadorainvest.ui.theme.CalculadoraInvestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraInvestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InvestResultsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun InvestResultsScreen(modifier: Modifier = Modifier) {
    var codigoAcao by remember { mutableStateOf("VALE3") }
    var cotacaoAtual by remember { mutableStateOf<Double?>(null) }
    var dividendos by remember { mutableStateOf<List<Dividendo>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            obterDadosAcao(codigoAcao) { resposta ->
                if (resposta != null) {
                    cotacaoAtual = resposta.valorCotacao
                    dividendos = resposta.dividendos
                } else {
                    errorMessage = "Erro ao obter os dados da ação."
                }
            }
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Cotação atual: ${cotacaoAtual ?: "Carregando..."}", style = MaterialTheme.typography.headlineMedium)

        dividendos?.let {
            Text(text = "Dividendos:", style = MaterialTheme.typography.headlineSmall)
            it.forEach { dividendo ->
                Text(text = "Data de pagamento: ${dividendo.dataPagamento}, Valor: R$ ${dividendo.valor}")
            }
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculadoraInvestTheme {
        InvestResultsScreen()
    }
}

fun obterDadosAcao(codigoAcao: String, callback: (AcaoResponse?) -> Unit) {

    val call = RetrofitClient.instance.getAcao(codigoAcao)

    call.enqueue(object : Callback<AcaoResponse> {
        override fun onResponse(call: Call<AcaoResponse>, response: Response<AcaoResponse>) {
            if (response.isSuccessful) {
                val acao = response.body()
                callback(acao)
            } else {
                println("Erro na resposta da API: ${response.code()} - ${response.message()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<AcaoResponse>, t: Throwable) {
            println("Falha na requisição: ${t.message}")
            callback(null)
        }
    })
}
