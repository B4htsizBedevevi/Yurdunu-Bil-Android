-- Seed additional reusable notification templates for the admin-managed automation pool.

insert into public.notification_templates (category,title,body,action,active,weight)
select v.category,v.title,v.body,'home',true,1
from (values
('study','Bugün 15 soru yeter 📚','Kısa bir tur yap: 15 soru çöz, yanlışlarına hemen göz at.'),
('study','Ders masası seni bekliyor','Kütüphaneden tek bir konu seç ve 10 dakikalık tekrar başlat.'),
('study','Bugünün hedefini koy','Kendine küçük bir hedef belirle. Bitirince XP kazanmayı unutma.'),
('streak','Serin bugün de devam etsin 🔥','Dünkü emeğini korumak için birkaç soru bile yeter.'),
('streak','Seri alarmı!','Bugünkü çalışmanı tamamla ve serini bir gün daha uzat.'),
('streak','Bir gün daha, bir adım daha','Küçük ama düzenli tekrarlar sınav gününde büyük fark yaratır.'),
('quiz','Kendini 10 soruda ölç','Hazırsan mini teste gir. Bakalım bugün kaç doğru çıkaracak?'),
('quiz','Hızlı tur zamanı ⏱️','10 soru, birkaç dakika, bolca pratik. Başlayalım.'),
('quiz','Yanlışlarını avantaja çevir','Bugün bir test çöz ve özellikle zorlandığın soruların konusunu tekrar et.'),
('geography','Haritadan bir il seç 🗺️','Rastgele bir il belirle ve bölgesi, yer şekilleri ve iklimini hatırla.'),
('geography','Türkiye turu başlıyor','Bugün bir coğrafya başlığı seç ve harita üzerinden pekiştir.'),
('geography','Bir bölgeyi avucunun içine al','Seçtiğin bölgenin en önemli özelliklerini 5 dakikada tekrar et.'),
('arena','Arena meydan okuması ⚔️','Bilgini rakiplere karşı denemek için bir Arena maçı aç.'),
('arena','Rating zamanı','Bugün bir maç daha yap. Her doğru cevap seni biraz daha yukarı taşır.'),
('content','Kütüphanede keşfedilecekler var','Uzun ders yerine tek bir başlık seç, oku ve hemen kendini yokla.'),
('content','Yeni konu seç','Bilmediğin bir başlığa dokun. Öğrendiklerini sorularla sağlamlaştır.'),
('motivation','Bugün senden tek isteğimiz var 💚','Mükemmel bir çalışma gerekmiyor. Sadece başla ve birkaç soru çöz.'),
('motivation','5 dakika bile sayılır','Bugün ayıracağın küçük bir zaman, yarınki yükünü azaltır.'),
('motivation','Hedefin hâlâ orada','Bir soru daha, bir tekrar daha. KPSS hedefin için devam et.')
) as v(category,title,body)
where not exists (
  select 1 from public.notification_templates t
  where t.category=v.category and t.title=v.title
);
