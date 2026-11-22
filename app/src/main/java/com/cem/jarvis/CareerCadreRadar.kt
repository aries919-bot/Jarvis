package com.cem.jarvis

/**
 * Resmî kaynaklarda yayımlanmış geçmiş merkezi yerleştirme tablolarındaki unvan örnekleri.
 * Bu katalog gelecek alım vaadi değildir; her yeni kılavuz kendi kadro tablosuyla doğrulanmalıdır.
 */
data class CareerCadreEntry(
    val title: String,
    val institutionExamples: String,
    val requirementNote: String
)

object CareerCadreRadar {
    const val PREFERENCE_WINDOW = "17–24 Aralık 2026"
    const val PREFERENCE_STATUS = "Tercih tarihleri ÖSYM takviminde yayımlandı; KPSS-2026/2 kadro ve kontenjan tablosu ayrıca bekleniyor."
    const val OFFICIAL_CALENDAR_URL = "https://www.osym.gov.tr/Sayfa/SinavTakvimi"
    const val KPSS_2026_1_MIDDLE_TABLE_URL = "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH1/tablo1_t1d03072026.pdf"
    const val KPSS_2026_1_ASSOCIATE_TABLE_URL = "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH1/tablo2_t1d03062026.pdf"
    const val ADALET_2026_NOTICE_URL = "https://pgm.adalet.gov.tr/Home/SayfaDetay/2026sozlesmelitasra"
    const val ISKUR_URL = "https://esube.iskur.gov.tr/"
    const val P94_ELECTRIC_ELECTRONICS_CODE = 2023
    const val P93_ELECTRIC_PROGRAM_CODE = 3290
    const val P93_ELECTRIC_TEDAS_NOTE =
        "2026/1 TABLO-2'de TEDAŞ Tekniker kadrolarında 3290 kodu kullanıldı. ÖSYM'nin 3290 tanımı: Elektrik veya Elektrik Teknolojisi ön lisans programından mezun olmak. Mezuniyet ve diğer özel koşullar sağlanmadan uygun sayılmaz."

    const val P94_ELECTRIC_CODE_NOTE =
        "2026/1 ortaöğretim nitelik kodu 2023; Elektrik-Elektronik Teknolojisi alan ve dalları ile belirtilen Endüstriyel Otomasyon dallarını kapsar. Diploma alanı/dalı kod açıklamasına tam uymalıdır."

    // 2026/1 ÖSYM TABLO-1: Ortaöğretim mezunlarının tercih edebildiği unvan örnekleri.
    val p94History = listOf(
        CareerCadreEntry(
            "Teknisyen",
            "TCDD Taşımacılık, DHMİ, Et ve Süt Kurumu, Gelir İdaresi, Hazine ve Maliye, SGK, Tarım İşletmeleri, Toprak Mahsulleri Ofisi",
            "Elektrik/pano alanı dâhil olmak üzere istenen lise alanı-dalı ve nitelik kodu ilan bazında kontrol edilmeli."
        ),
        CareerCadreEntry(
            "Vagon teknisyeni",
            "TCDD Taşımacılık",
            "2026/1 tablosunda demiryolu alanı ve özel nitelik kodlarıyla yer aldı; elektrik teknisyeniyle aynı şart kabul edilmemeli."
        ),
        CareerCadreEntry("Destek personeli", "Üniversiteler, SGK, Meteoroloji ve diğer kurumlar", "Görev tanımı; temizlik, bakım, taşıma veya farklı destek işleri olabilir. Özel şartlar okunmalı."),
        CareerCadreEntry("Koruma ve güvenlik görevlisi", "Üniversiteler ve kamu kurumları", "Özel güvenlik kimlik kartı, cinsiyet/yaş ve diğer şartlar ilana göre değişebilir."),
        CareerCadreEntry("Şoför", "Hazine ve Maliye, Türk Tarih Kurumu ve il özel idareleri", "Sürücü belgesi sınıfı, belge tarihi ve varsa SRC/psikoteknik şartı kontrol edilmeli."),
        CareerCadreEntry("Aşçı", "Gelir İdaresi Başkanlığı", "Aşçılık alanı, ustalık/sertifika ve deneyim şartları ilana göre kontrol edilmeli."),
        CareerCadreEntry("Hizmetli", "Gelir İdaresi ve diğer kamu kurumları", "Öğrenim ve görev şartı her ilan için ayrıca okunmalı."),
        CareerCadreEntry("Bekçi", "Çankırı İl Özel İdaresi", "Bu unvanın her dönem ve kurumda açılacağı anlamına gelmez."),
        CareerCadreEntry("Dağıtıcı", "Hazine ve Maliye Bakanlığı", "Görev tanımı ve diğer şartlar kılavuzdan doğrulanmalı."),
        CareerCadreEntry("Bahçıvan", "Hazine ve Maliye Bakanlığı", "Alan/sertifika şartı ilan metninden kontrol edilmeli."),
        CareerCadreEntry("Veteriner sağlık teknisyeni", "Tarım İşletmeleri Genel Müdürlüğü", "Veteriner sağlık alanı/dalı şartı ayrıca aranır."),
        CareerCadreEntry("Sağlık memuru", "Türkiye Taşkömürü Kurumu", "Sağlık alanı ve diploma şartları ilana özel kontrol edilmeli.")
    )

    // 2026/1 ÖSYM TABLO-2: Ön lisans tablosunda görülen unvan örnekleri.
    val p93History = listOf(
        CareerCadreEntry("Tekniker", "TCDD Taşımacılık, DHMİ, EÜAŞ, TEİAŞ, Tapu ve Kadastro, TİGEM, Et ve Süt Kurumu", "Program kodları çok önemlidir; her tekniker kadrosu Elektrik programı mezunlarına açık değildir."),
        CareerCadreEntry("Tekniker · Elektrik (3290)", "Türkiye Elektrik Dağıtım A.Ş. (TEDAŞ)", P93_ELECTRIC_TEDAS_NOTE),
        CareerCadreEntry("Sağlık teknikeri", "Kafkas Üniversitesi ve Nevşehir Hacı Bektaş Veli Üniversitesi", "Sağlık ön lisans programı ve unvan şartı aranır."),
        CareerCadreEntry("Büro personeli", "Sosyal Güvenlik Kurumu ve üniversiteler", "Büro yönetimi veya diğer program/nitelik kodları ilanın özel şartına göre değişir."),
        CareerCadreEntry("İcra memuru", "Sosyal Güvenlik Kurumu", "Özel program ve nitelik kodları bulunur."),
        CareerCadreEntry("Sekreter", "Rekabet Kurumu", "İlgili ön lisans programı ve nitelik kodu kontrol edilmeli."),
        CareerCadreEntry("İtfaiyeci", "Elektrik Üretim A.Ş. (EÜAŞ)", "İtfaiyecilik programı, ehliyet ve fiziksel şartlar ilana göre kontrol edilmeli."),
        CareerCadreEntry("Veri hazırlama ve kontrol işletmeni", "Türk Dil Kurumu ve diğer kurumlar", "Bilgisayar sertifikası/öğrenim ve nitelik kodu şartları kontrol edilmeli."),
        CareerCadreEntry("Dava takip memuru", "Türkiye Elektrik Dağıtım A.Ş. (TEDAŞ)", "İlgili program ve nitelik kodu şartı aranır."),
        CareerCadreEntry("Muhasebeci", "Toprak Mahsulleri Ofisi", "Muhasebe alanı/programı şartı ilan tablosundan doğrulanmalı."),
        CareerCadreEntry("Puantör", "Tarım İşletmeleri Genel Müdürlüğü", "İlanda belirtilen ön lisans programları kontrol edilmeli."),
        CareerCadreEntry("Koruma ve güvenlik görevlisi", "Üniversiteler, SGK ve diğer kurumlar", "Özel güvenlik kimlik kartı ve diğer koşullar ilan bazında doğrulanmalı."),
        CareerCadreEntry("Destek personeli", "Sosyal Güvenlik Kurumu ve diğer kurumlar", "Görev tanımı ve aranılan öğrenim düzeyi kadro satırına göre değişir.")
    )

    val recruitmentRoutes = listOf(
        "KPSS merkezi yerleştirme" to "ÖSYM tercih kılavuzu, öğrenim tablosu, nitelik kodu ve kontenjanla takip edilir.",
        "4/B sözleşmeli personel" to "Kurum ilanı, Kariyer Kapısı başvurusu, ön değerlendirme ve/veya sözlü-uygulamalı sınav süreci olabilir.",
        "Sürekli işçi" to "İŞKUR ilanı üzerinden; noter kurası, sınav veya kurumun açıkladığı diğer aşamalarla ilerleyebilir.",
        "KİT sözleşmeli personel" to "Kurum ve istihdam şekline göre ayrı şartlarla yayımlanır; otomatik olarak P94/P93 sayılmaz.",
        "Belediye ve belediye şirketi" to "Belediye/şirket ilanı, İŞKUR ve varsa kura/sınav duyuruları ayrı kontrol edilir.",
        "Özel sektör" to "İşverenin kendi kariyer sayfası ve iş ilanı platformlarından doğrulanır."
    )

    const val P94_ELAZIG_TECHNICIAN_2026_1_LOWEST = 76.91127
    const val P94_ELAZIG_TECHNICIAN_NOTE =
        "KPSS-2026/1 geçmiş tablosunda Elazığ TCDD Taşımacılık teknisyen kadrosunun yerleştirme puanı 76,91127 idi. Bu geçmiş dönem verisidir; KPSS-2026/2 için taban puan tahmini değildir."

    fun associateTrackLabel(isAssociateDegreeGraduate: Boolean): String =
        if (isAssociateDegreeGraduate) {
            "P93 · Ön lisans mezunu olarak, kılavuzdaki program kodları ve diğer şartlar kontrol edilerek değerlendirilir."
        } else {
            "MEZUNİYET SONRASI · Ön lisans kadroları için tercih tarihinde mezuniyet şartı aranabilir. Mezuniyet gerçekleşmeden P93 kadrosu uygun kabul edilmez."
        }

    fun kpss2026_2CadreStatus(hasOfficialGuide: Boolean): String =
        if (hasOfficialGuide) {
            "ÖSYM kılavuzu yayımlandı. Kadro, kontenjan, puan türü ve nitelik kodları resmî tablodan okunmalı."
        } else {
            PREFERENCE_STATUS
        }
}
