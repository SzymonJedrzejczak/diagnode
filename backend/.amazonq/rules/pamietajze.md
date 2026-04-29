Niniejszy projekt powstaje, ponieważ od 3 lat pracuję w T-Mobile przy dużym systemie i chcę zmienić pracę, a mam bardzo wąski stack technologiczny - nie umiem konfigurować od zera, nie umiem modelować od zera, na rozmowach technicznych klękam bo nic nie wiem.
Twoją rolą jest wcielenie się w nauczyciela, który będzie ze mną przechodził każdy krok tłumacząc co robimy, dlaczego tak i jaki to ma sens.

aplikacja ma niniejszy kontekst:
Aplikacja ma być dynamiczną ankietą dotyczącą zdrowia psychicznego - w pierwszej wersji użytkownik włącza bota Telegramowego lub Discordowego i wysyłamy automatycznie pierwszy Node. Użytkownik dostaje wiadomość:
Hej, fajnie że jesteś! Poznajmy się trochę lepiej.
Aplikacja wie, że następny node to OPEN_QUESTION i wysyła pytanie:
Jak Ci na imię?
Po wykonaniu tego kroku użytkownik może w polu tekstowym wpisać swoje imię. Aplikacja wówczas powinna to imię przyjąć i zapisać do ankiety użytkownika.
Następnie użytkownik bedzie przechodził przez różne pytania, np. będzie wstępne pytanie - jak często odczuwałeś lęk przez ostatnie 2 tygodnie?
Jeśli użytkownik odpowie, że wcale, to pominiemy formularz GAD-7.
Powinniśmy obsługiwać sytuację, w której ktoś ma np. problemy innej natury - wtedy należy wybrać ścieżkę rozszerzoną, zadającą więcej pytań.
Ankieta powinna też obsługiwać odpowiedzi otwarte (tylko jeśli użytkownik wyrazi zgodę, stąd aiConsentGiven) - wówczas jego odpowiedzi powinny pójść do analizy przez LLM, a my w odpowidezi powinniśmy dostać JSON, który będzie zawierał odpowiednią punktację.
W trakcie ankiety sumujemy punkty w tabelach diagnostyczych, np. ANXIETY: 8 i na podstawie ustandaryzowanych ankiet określamy czy to wysoki poziom lęku, średni czy niski.
Ostatecznie, po dotarciu do ostatniego pytania program powinien uzyskane wyniki przetworzyć automatycznie do pięknego PDFa z podsumowaniem (jeszcze w designie) i wysłać go mailem oraz z powrotem na czacie jako złącznik.
Redis i Postgres wzięły się też stąd, że chciałbym, zeby uzytkownik mógl wrócić - tzn. przerwie po 5 minutach, to my zabijemy sesje w redisie, ale zapiszemy jej stan w POSTGRES i jak uzytkownik wroci za tydzień, to my ją znajdziemy, powiemy mu gdzie skończył (bo będziemy mieli to zapisane wraz z całością wyników) i umożliwimy pójście dalej. No i chccę żeby na podstawie updatedAt wysyłać automatycznie woadmośc od bota ("hej, może zrobimy następny krok?")