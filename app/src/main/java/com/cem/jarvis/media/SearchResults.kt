package com.cem.jarvis.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/*
 * Arama sonucu kartı: başlık, "TV Dizisi · 2026 · Komedi · Suç", özet, sağda afiş,
 * altta sezon kutuları (1, 2, …, 7, 8) ve "İzle ›" butonu.
 * FilmDizi.kt'deki SearchScreen, arama yapılınca otomatik bunu kullanır.
 * Kart veya "İzle"ye basınca detay sayfası açılır (fragman, nerede izlenir, bölümler).
 */
private val GENRES = mapOf(
    28 to "Aksiyon", 12 to "Macera", 16 to "Animasyon", 35 to "Komedi", 80 to "Suç", 99 to "Belgesel",
    18 to "Dram", 10751 to "Aile", 14 to "Fantastik", 36 to "Tarih", 27 to "Korku", 10402 to "Müzik",
    9648 to "Gizem", 10749 to "Romantik", 878 to "Bilim Kurgu", 53 to "Gerilim", 10752 to "Savaş",
    37 to "Vahşi Batı", 10759 to "Aksiyon ve Macera", 10762 to "Çocuk", 10764 to "Reality",
    10765 to "Bilim Kurgu ve Fantazi", 10766 to "Pembe Dizi", 10768 to "Savaş ve Politika"
)

@Composable
fun SearchResultCard(item: Item, onOpen: () -> Unit) {
    val isMovie = item.media_type == "movie" || item.title != null
    var seasons by remember(item.id) { mutableIntStateOf(0) }
    LaunchedEffect(item.id) { if (!isMovie && item.poster_path?.startsWith("http") != true) runCatching { seasons = api.tvInfo(item.id).number_of_seasons ?: 0 } }

    val meta = listOfNotNull(
        if (isMovie) "Film" else "TV Dizisi",
        item.year.ifBlank { null },
        *item.genre_ids.orEmpty().mapNotNull { GENRES[it] }.take(2).toTypedArray()
    ).joinToString(" · ")

    Column(
        Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1B1E2B))
            .clickable(onClick = onOpen)
            .semantics(mergeDescendants = true) {
                contentDescription = "${item.label}, ${meta.ifBlank { if (isMovie) "Film" else "TV dizisi" }}. Ayrıntıları aç"
            }
            .padding(16.dp)
    ) {
        Row {
            Column(Modifier.weight(1f).padding(end = 12.dp)) {
                Text(item.label, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(meta, color = Color(0xFF9AA1B2), fontSize = 13.sp, modifier = Modifier.padding(vertical = 6.dp))
                Text(item.overview.orEmpty().ifBlank { "Özet bulunamadı." }, color = Color(0xFFD5D9E4), fontSize = 14.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
            }
            AsyncImage(webImg(item.poster_path), item.label, Modifier.size(88.dp, 128.dp).clip(RoundedCornerShape(12.dp)))
        }
        if (seasons > 0) {
            // 1 2 … 7 8 şeklinde kısalt
            val shown: List<String> = if (seasons <= 5) (1..seasons).map { "$it" }
                else listOf("1", "2", "…", "${seasons - 1}", "$seasons")
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                shown.forEach { t ->
                    Text(t, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2E40)).padding(vertical = 10.dp))
                }
            }
        }
        Text("İzle  ›", color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2E40)).padding(vertical = 12.dp))
    }
}
