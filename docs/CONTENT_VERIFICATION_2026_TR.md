# Yurdunu Bil — 2026 İçerik Doğrulama Kaydı

**Son kontrol:** 14 Eylül 2026

Bu belge, soru bankasının yapısal bütünlüğü ile güncel veri sorularının birinci taraf resmî kaynaklara göre kontrol edildiğini kaydeder.

## Otomatik kalite kapısı

- Aktif Supabase soru sayısı: **603** (son deduplikasyon kontrolü sonrası)
- Boş soru metni: **0**
- Geçersiz seçenek sayısı: **0**
- Geçersiz `correctIndex`: **0**
- Boş açıklama: **0**
- Aynı soru içindeki yinelenen seçenek: **0**
- Tam içerik tekrarı: **0**
- Güncel (`is_current=true`) aktif soru: **20**
- Güncel aktif sorularda eksik kaynak URL'si: **0**
- Güncel aktif sorularda eksik kaynak notu: **0**
- Güncel aktif sorularda eksik doğrulama zamanı: **0**

## Güncel veri kaynakları

Yalnızca birinci taraf kurum kaynakları esas alınır:

- TÜİK — ADNKS 2025
- TÜİK — İç Göç İstatistikleri 2025
- TÜİK — Uluslararası Göç İstatistikleri 2025
- TÜİK — Bitkisel Üretim 1. Tahmin 2026
- TÜİK — İşgücü İstatistikleri 2026
- TÜİK — GSYH II. Çeyrek 2026
- TÜİK — Turizm İstatistikleri 2026
- TÜİK — TÜFE Ağustos 2026
- T.C. Enerji ve Tabii Kaynaklar Bakanlığı — Elektrik İstatistikleri
- Meteoroloji Genel Müdürlüğü — Aylık Sıcaklık Analizi
- T.C. Tarım ve Orman Bakanlığı — Arıcılık İstatistikleri
- Harita Genel Müdürlüğü — İl/İlçe yüz ölçümleri

## Uygulama kuralı

Güncel veri değişebilen sorulara yıl bilgisi eklenir ve soru mümkün olduğunca resmî kaynağın yayımladığı değerle birebir eşleştirilir. Yeni güncel soru eklenirken kaynak URL'si, kaynak notu ve doğrulama zamanı Supabase kaydına işlenmelidir.

Yapısal doğrulama `QuestionBankValidator` tarafından uygulama soru havuzu oluşturulurken zorunlu tutulur. Tam içerik tekrarı soru metni + seçenek kümesi + doğru seçenek imzasıyla kontrol edilir; yalnızca aynı köke sahip fakat farklı seçenekleri olan sorular otomatik olarak silinmez.
