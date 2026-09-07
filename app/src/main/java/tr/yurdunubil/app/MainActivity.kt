package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { YurdunuBilApp() }
    }
}

@Composable
private fun YurdunuBilApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var quizOpen by remember { mutableStateOf(false) }
    val tabs = listOf("Ana Sayfa", "Dersler", "Arena", "İstatistik", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.Explore, Icons.Default.SportsEsports, Icons.Default.BarChart, Icons.Default.Person)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (quizOpen) {
                QuizScreen(onExit = { quizOpen = false })
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            0 -> HomeScreen(onStartQuiz = { quizOpen = true })
                            else -> PlaceholderScreen(tabs[selectedTab])
                        }
                    }
                    NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                        tabs.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(icons[index], contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onStartQuiz: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Yurdunu Bil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("KPSS Önlisans 2026", style = MaterialTheme.typography.bodyLarge)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Bugünkü hedefin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("10 soru çöz • XP kazan • serini koru")
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth()) {
                        Text("Teste Başla")
                    }
                }
            }
        }
        item { Text("Hızlı Başla", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item { QuickCard("Türkçe", "Paragraf ve anlam soruları") }
        item { QuickCard("Matematik", "Temelden soru pratiği") }
        item { QuickCard("Genel Kültür", "Tarih • Coğrafya • Vatandaşlık") }
    }
}

@Composable
private fun QuickCard(title: String, subtitle: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun QuizScreen(onExit: () -> Unit) {
    val engine = remember { QuizEngine(SampleQuestions.all) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<QuizResult?>(null) }
    val question = engine.current

    if (finished && result != null) {
        ResultScreen(result = result!!, onExit = onExit)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Soru ${engine.currentIndex + 1} / ${engine.size}", fontWeight = FontWeight.Bold)
            Text("+${if (selected == question.correctIndex) 10 else 0} XP")
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(question.subject, style = MaterialTheme.typography.labelLarge)
        Text(question.topic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(18.dp))
        Text(question.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(question.options) { index, option ->
                val correct = index == question.correctIndex
                val selectedWrong = answered && selected == index && !correct
                val container = when {
                    answered && correct -> MaterialTheme.colorScheme.primaryContainer
                    selectedWrong -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable(enabled = !answered) { selected = index },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = container)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${'A' + index}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(option)
                    }
                }
            }
        }

        if (answered) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(question.explanation, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (!answered) {
                    engine.answer(selected)
                    answered = true
                } else if (engine.currentIndex == engine.size - 1) {
                    result = engine.result()
                    finished = true
                } else {
                    selected = null
                    answered = false
                }
            },
            enabled = answered || selected != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (!answered) "Cevabı Kontrol Et" else if (engine.currentIndex == engine.size - 1) "Sonucu Gör" else "Sonraki Soru")
        }
    }
}

@Composable
private fun ResultScreen(result: QuizResult, onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Test Tamamlandı!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text("${result.correct} doğru • ${result.wrong} yanlış • ${result.blank} boş")
        Spacer(modifier = Modifier.height(8.dp))
        Text("+${result.xp} XP", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("Ana Sayfaya Dön") }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Bu bölüm sıradaki geliştirmelerde doldurulacak.")
        }
    }
}
