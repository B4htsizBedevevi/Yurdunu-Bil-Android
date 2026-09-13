-- Stable, low-noise local notification cadence.
-- Four useful study windows; the client honors days_mask and skips redundant reminders.

insert into public.notification_automations (key,title,category,time_local,active,days_mask)
values
('morning_focus','Sabah Odak','study','08:45',true,127),
('midday_challenge','Öğle Mini Test','quiz','13:30',true,127),
('evening_geography','Akşam Coğrafya Molası','geography','18:15',true,127),
('night_review','Akşam Tekrarı','streak','20:45',true,127)
on conflict (key) do update
set title=excluded.title,
    category=excluded.category,
    time_local=excluded.time_local,
    active=excluded.active,
    days_mask=excluded.days_mask,
    updated_at=now();

-- Keep the older schedule entries useful but move them into the new cadence.
update public.notification_automations
set time_local='08:45', category='study', title='Sabah Odak', updated_at=now()
where key='morning_study';

update public.notification_automations
set time_local='13:30', category='quiz', title='Öğle Mini Test', updated_at=now()
where key='afternoon_quiz';

update public.notification_automations
set time_local='20:45', category='streak', title='Akşam Tekrarı', updated_at=now()
where key='evening_review';

insert into public.notification_templates (category,title,body,action,active,weight)
select v.category,v.title,v.body,'home',true,1
from (values
('study','Sabah turunu başlat','Bugün tek bir konu seç. 10 soru çözerek güne küçük bir avantajla başla.'),
('study','Bugünün ilk 10 sorusu','Kısa bir başlangıç yap: 10 soru, ardından yanlışlarına hızlıca bak.'),
('study','Haritayla güne başla','Kütüphaneden bir Türkiye coğrafyası konusu aç ve 5 dakikalık tekrar yap.'),
('study','Bugün de ritim sende','Uzun bir çalışma şart değil. Küçük bir test bile düzenini korur.'),
('study','Zayıf konuna dön','Performansın düşük olan bir başlığı seç ve hemen ardından mini test çöz.'),
('study','KPSS için küçük bir adım','Bugünün hedefini küçült: bir konu, on soru, temiz bir tekrar.'),

('quiz','Öğle mini testi hazır','10 soruluk kısa turla bugünkü bilgini ölç. Sonuçta yanlışlarına da mutlaka bak.'),
('quiz','Kaç doğru çıkaracaksın?','Birkaç dakikan var. Karşına çıkan soruları hızla ama dikkatli çöz.'),
('quiz','Hızlı yoklama zamanı','Öğrendiklerini kalıcı hale getirmek için kısa bir soru turu başlat.'),
('quiz','Bugünün ikinci turu','Sabah çalıştıysan şimdi kendini yokla; çalışmadıysan buradan başla.'),
('quiz','Yanlışlar puan değil ipucu','Bir mini test çöz ve hangi konuya geri dönmen gerektiğini gör.'),
('quiz','10 soruluk meydan okuma','Süreyi çok uzatma. 10 soru çöz, sonucu gör ve bir sonraki adımı seç.'),

('geography','Akşam harita molası','Bugün bir il seç. Bölgesi, iklimi, yer şekilleri ve öne çıkan özelliklerini hatırla.'),
('geography','Türkiye turunun yeni durağı','Kütüphaneden bir bölge seç ve bilgilerini harita üzerinde zihninde canlandır.'),
('geography','Bir il, üç bilgi','Bir şehir belirle ve onunla ilgili üç coğrafi bilgiyi kendi kendine söyle.'),
('geography','Haritayı zihninde aç','Dağlar, ovalar, göller ve akarsular arasında bağlantı kurmaya çalış.'),
('geography','Coğrafyada bağlantı kur','Ezberlemek yerine neden-sonuç ilişkisi kur. Sonra birkaç soru ile pekiştir.'),
('geography','Günün coğrafya notu','Tek bir başlık seç, kısa tekrar yap ve öğrendiğini hemen soruyla sınayalım.'),

('streak','Serin bugün de sende','Bugünkü çalışmanı tamamla ve dünkü emeğini boşa çıkarma.'),
('streak','Günü boş bırakma','Birkaç soru bile yeter. Önemli olan düzenli olarak devam etmek.'),
('streak','Akşam yoklaması','Bugün hedefinin ne kadarını tamamladın? Eksikse kısa bir tur daha yap.'),
('streak','Bir gün daha eklensin','Küçük bir tekrar ile çalışma serini koruyabilirsin.'),
('streak','Bugün de tamamla','Günü kapatmadan önce yanlışlarına ve zayıf konularına bir kez daha bak.'),
('streak','Yarınını bugünden kolaylaştır','Bugünkü kısa tekrar, yarın hatırlamayı çok daha kolay hale getirir.')
) as v(category,title,body)
where not exists (
  select 1 from public.notification_templates t
  where t.category=v.category and t.title=v.title
);
