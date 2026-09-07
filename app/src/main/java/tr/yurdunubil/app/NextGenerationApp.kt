package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val Deep = Color(0xFF06271F)
private val Green = Color(0xFF16B87A)
private val Mint = Color(0xFFDDF9EA)
private val Gold = Color(0xFFFFC857)
private val Red = Color(0xFFE45454)
private val Bg = Color(0xFFF3F8F5)

@Composable
fun NextGenerationApp() {
    val context = LocalContext.current
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    var review by remember { mutableStateOf(false) }
    val start: (String,List<Question>,Boolean) -> Unit = { t, list, r ->
        val safe = list.filter { it.options.size >= 2 && it.correctIndex in it.options.indices }.distinctBy { it.id }
        if (safe.isNotEmpty()) { quizTitle = t; quiz = safe; review = r }
    }
    if (quiz != null) { QuizScreen(quizTitle, quiz!!, store, review) { quiz = null }; return }
    val labels = listOf("Ana Sayfa","Kütüphane","Harita","Arena","Profil")
    val icons = listOf(Icons.Default.Home,Icons.Default.MenuBook,Icons.Default.Map,Icons.Default.SportsEsports,Icons.Default.Person)
    Scaffold(containerColor = Bg, bottomBar = { NavigationBar(containerColor = Color.White) { labels.forEachIndexed { i,l -> NavigationBarItem(tab == i,{tab=i},{Icon(icons[i],l)},{Text(l,fontSize=10.sp)}) } } }) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) { when(tab) { 0->Home(state,start);1->Library(start);2->Atlas(start);3->Arena(start);else->Profile(state,start) } }
    }
}

@Composable private fun Home(s: AppProgressStore.Snapshot,start:(String,List<Question>,Boolean)->Unit) {
    val accuracy=if(s.solved==0)0 else (s.correct*100f/s.solved).roundToInt()
    val wrong=SharedQuestionPool.all.filter{it.id in s.wrongIds}
    val smart=wrong.ifEmpty{SharedQuestionPool.all.shuffled().take(10)}
    LazyColumn(contentPadding=PaddingValues(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item{ Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Deep,Color(0xFF0C5E46))),RoundedCornerShape(bottomStart=30.dp,bottomEnd=30.dp)).padding(22.dp)){Column{Text("YURDUNU BİL",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black);Text("KPSS Önlisans • Türkiye Coğrafyası",color=Mint);Spacer(Modifier.height(14.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Pill("${s.xp} XP");Pill("🔥 ${s.streak}");Pill("%$accuracy")}}}}}
        item{SectionTitle("Bugünün hedefi","10 soru • serini koru")}
        item{Row(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){ActionCard("⚡","Hızlı 10","Karışık",Green,Modifier.weight(1f)){start("Hızlı 10",SharedQuestionPool.all.shuffled().take(10),false)};ActionCard("🧠","Akıllı Tekrar","${smart.size} soru",Gold,Modifier.weight(1f)){start("Akıllı Tekrar",smart.take(15),true)}}}
        item{ActionCard("🔁","Yanlışlarım","${wrong.size} soru",Red,Modifier.fillMaxWidth().padding(horizontal=18.dp)){if(wrong.isNotEmpty())start("Yanlışlarım",wrong.take(20),true)}}
        item{Panel(Mint){Text("📊 PERFORMANS",color=Green,fontWeight=FontWeight.Black);Text("%$accuracy doğruluk",color=Deep,fontSize=23.sp,fontWeight=FontWeight.Black);Text("${s.solved} soru • ${s.correct} doğru • ${s.wrong} yanlış",color=Color.Gray)}}
        item{Panel(Deep){Text("🧠 ZAYIF NOKTA",color=Gold,fontWeight=FontWeight.Black);Text(s.topicWrong.maxByOrNull{it.value}?.key?:"Henüz belirlenmedi",color=Color.White,fontSize=21.sp,fontWeight=FontWeight.Black);Text("Soru çözdükçe öneriler kişiselleşir.",color=Mint)}}
    }
}

@Composable private fun Library(start:(String,List<Question>,Boolean)->Unit){
    var q by remember{mutableStateOf("")};val topics=GeographyData.topics.filter{it.title.contains(q,true)||it.subtitle.contains(q,true)}
    LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text("Kütüphane",color=Deep,fontSize=31.sp,fontWeight=FontWeight.Black);Text("Oku • bağlantı kur • test et",color=Green,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));OutlinedTextField(q,{q=it},Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Default.Search,null)},placeholder={Text("Dağ, ova, maden, nüfus…")})};item{Panel(Deep){Text("🇹🇷 TÜRKİYE COĞRAFYA ATLASI",color=Gold,fontWeight=FontWeight.Black);Text("${topics.size} konu",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Black);Text("Konu seç ve hemen test et.",color=Mint)}};items(topics,key={it.title}){t->Panel(Color.White,{start(t.title,SharedQuestionPool.all.shuffled().take(10),false)}){Row(verticalAlignment=Alignment.CenterVertically){Text(t.icon,fontSize=27.sp);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(t.title,color=Deep,fontWeight=FontWeight.Black,fontSize=17.sp);Text(t.subtitle,color=Color.Gray,fontSize=12.sp);Text("${t.lessons.size} alt başlık • %${t.progress}",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)};Icon(Icons.Default.ArrowForward,null,tint=Green)}}}}
}

@Composable private fun Atlas(start:(String,List<Question>,Boolean)->Unit){
    var region by remember{mutableStateOf("Tümü")};val regions=listOf("Tümü","Marmara","Ege","Akdeniz","İç Anadolu","Karadeniz","Doğu Anadolu","Güneydoğu Anadolu");val ps=GeographyData.provinces.filter{region=="Tümü"||it.region==region}
    LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text("Türkiye Atlası",color=Deep,fontSize=31.sp,fontWeight=FontWeight.Black);Text("İl → bölge → özellik → soru",color=Green,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){regions.forEach{r->FilterChip(region==r,{region=r},label={Text(r)})}}};item{Panel(Brush.horizontalGradient(listOf(Deep,Color(0xFF0C5E46)))){Text("🗺️ ${ps.size} il",color=Gold,fontSize=24.sp,fontWeight=FontWeight.Black);Text("Kartı aç ve mini teste geç.",color=Mint)}};items(ps,key={it.name}){p->Panel(Color.White,{start("${p.name} Mini Test",SharedQuestionPool.all.shuffled().take(5),false)}){Row(verticalAlignment=Alignment.CenterVertically){Text("📍",fontSize=24.sp);Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(p.name,color=Deep,fontWeight=FontWeight.Black);Text(p.region,color=Green,fontSize=11.sp);Text(p.clue,color=Color.Gray,fontSize=11.sp)};Icon(Icons.Default.PlayArrow,null,tint=Green)}}}}
}

@Composable private fun Arena(start:(String,List<Question>,Boolean)->Unit){LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text("Arena",color=Deep,fontSize=31.sp,fontWeight=FontWeight.Black);Text("Hız • bilgi • seri",color=Green,fontWeight=FontWeight.Bold)};item{Panel(Deep){Text("🏆 SEZON 1",color=Gold,fontWeight=FontWeight.Black);Text("Türkiye Ustası",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.Black);Text("Sahte rakip yok. Online PvP katmanı gerçek backend ile bağlanacak.",color=Mint)}};GeographyData.games.forEach{g->item{Panel(Color.White,{start(g.title,SharedQuestionPool.all.shuffled().take(10),false)}){Row(verticalAlignment=Alignment.CenterVertically){Text(g.icon,fontSize=27.sp);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(g.title,color=Deep,fontWeight=FontWeight.Black,fontSize=17.sp);Text(g.subtitle,color=Color.Gray,fontSize=12.sp)};Icon(Icons.Default.PlayArrow,null,tint=Green)}}}};item{Text("1v1, Hız Arenası, Bölge Savaşı ve Türkiye Ustası aynı güvenli soru motorunu paylaşır.",color=Color.Gray,fontSize=11.sp)}}
}

@Composable private fun Profile(s:AppProgressStore.Snapshot,start:(String,List<Question>,Boolean)->Unit){val accuracy=if(s.solved==0)0 else(s.correct*100f/s.solved).roundToInt();val wrong=SharedQuestionPool.all.filter{it.id in s.wrongIds};LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text("Profil & Gelişim",color=Deep,fontSize=31.sp,fontWeight=FontWeight.Black);Text("Seviye ${1+s.xp/500} • %$accuracy doğruluk",color=Green,fontWeight=FontWeight.Bold)};item{Panel(Deep){Text("${s.xp} XP",color=Color.White,fontSize=29.sp,fontWeight=FontWeight.Black);Text("${s.solved} soru • ${s.correct} doğru • ${s.wrong} yanlış",color=Mint)}};item{Panel(Color.White){Text("📈 KONU ANALİZİ",color=Green,fontWeight=FontWeight.Black);s.topicWrong.entries.sortedByDescending{it.value}.take(7).forEach{Text("${it.key}: ${it.value} yanlış",color=Deep,modifier=Modifier.padding(top=7.dp))}}};item{Panel(Color.White,{if(wrong.isNotEmpty())start("Yanlışlarım",wrong.take(20),true)}){Text("🔁 Yanlış soru deposu",color=Deep,fontWeight=FontWeight.Black);Text("${wrong.size} soru",color=Red,fontSize=20.sp,fontWeight=FontWeight.Black)}}}}
}

@Composable private fun QuizScreen(title:String,questions:List<Question>,store:AppProgressStore,review:Boolean,done:()->Unit){var i by remember{mutableIntStateOf(0)};var selected by remember{mutableIntStateOf(-1)};var answered by remember{mutableStateOf(false)};val scope=rememberCoroutineScope();val q=questions[i];Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)){Row(verticalAlignment=Alignment.CenterVertically){IconButton(done){Icon(Icons.Default.Close,"Kapat")};Column(Modifier.weight(1f)){Text(title,color=Deep,fontWeight=FontWeight.Black);Text("${i+1}/${questions.size}",color=Green,fontWeight=FontWeight.Bold)}};LinearProgressIndicator((i+1f)/questions.size,Modifier.fillMaxWidth(),color=Green,trackColor=Mint);Spacer(Modifier.height(20.dp));Text(q.topic,color=Green,fontWeight=FontWeight.Black);Text(q.text,color=Deep,fontSize=21.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(vertical=16.dp));q.options.forEachIndexed{n,opt->val correct=n==q.correctIndex;val bg=when{!answered->Color.White;correct->Mint;selected==n->Color(0xFFFFE1E1);else->Color.White};Card(colors=CardDefaults.cardColors(bg),shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().padding(vertical=4.dp).clickable(!answered){selected=n;answered=true;scope.launch{store.record(q,n==q.correctIndex)}}){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("${('A'.code+n).toChar()}",color=Deep,fontWeight=FontWeight.Black);Spacer(Modifier.width(12.dp));Text(opt,color=Deep,fontWeight=if(correct&&answered||selected==n)FontWeight.Bold else FontWeight.Normal)}}};if(answered){Spacer(Modifier.height(12.dp));Panel(if(selected==q.correctIndex)Mint else Color(0xFFFFEEEE)){Text(if(selected==q.correctIndex)"✓ DOĞRU" else "✕ YANLIŞ",color=if(selected==q.correctIndex)Green else Red,fontWeight=FontWeight.Black);Text("Dikkat Köşesi",color=Deep,fontWeight=FontWeight.Black);Text(q.explanation,color=Color.Gray,fontSize=13.sp)};Spacer(Modifier.weight(1f));Button({if(i+1<questions.size){i++;selected=-1;answered=false}else done()},Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(Green)){Text(if(i+1<questions.size)"Sonraki Soru" else "Tamamla")}}}}

@Composable private fun Panel(bg:Any,onClick:(()->Unit)?=null,content:@Composable ColumnScope.()->Unit){val m=when(bg){is Color->Modifier.background(bg);is Brush->Modifier.background(bg);else->Modifier.background(Color.White)};Box(Modifier.fillMaxWidth().then(m).let{if(onClick!=null)it.clickable(onClick=onClick)else it}.padding(17.dp)){Column(content=content)}}
@Composable private fun ActionCard(icon:String,title:String,sub:String,accent:Color,modifier:Modifier,onClick:()->Unit){Card(colors=CardDefaults.cardColors(Color.White),shape=RoundedCornerShape(20.dp),modifier=modifier.clickable(onClick=onClick)){Column(Modifier.padding(15.dp)){Text(icon,fontSize=25.sp);Text(title,color=Deep,fontWeight=FontWeight.Black);Text(sub,color=accent,fontSize=11.sp,fontWeight=FontWeight.Bold)}}}
@Composable private fun SectionTitle(a:String,b:String){Column(Modifier.padding(horizontal=18.dp)){Text(a,color=Deep,fontSize=19.sp,fontWeight=FontWeight.Black);Text(b,color=Color.Gray,fontSize=12.sp)}}
@Composable private fun Pill(t:String){Surface(color=Color.White.copy(.15f),shape=RoundedCornerShape(30.dp)){Text(t,color=Color.White,fontWeight=FontWeight.Bold,fontSize=12.sp,modifier=Modifier.padding(horizontal=10.dp,vertical=6.dp))}}
