# Discord-Musicbot


## kuvaus

Tämä on kaveriporukan Discord-palvelinta varten harrastusprojektina luotu yksinkertainen musiikkibotti. Botti on ensisijaisesti suunniteltu toimimaan, vain yhdellä palvelimella, mutta se on rakennettu niin, että sen käyttämisestä useammalla palvelimella ei pitäisi syntyä ongelmia. Musiikin toisto tapahtuu [LavaPlayer-kirjastoon](https://github.com/lavalink-devs/lavaplayer) avulla. Ohjelma kommunikoi discordin kanssa käyttämällä [JDA-kirjastoa](https://github.com/discord-jda/JDA).


## käytönaloitus

### 1. Luo Discord-botti
 **[Ohjeet Discor-dbotin luomiseen](https://discord.com/developers/docs/getting-started)**<br />
 Luo botille kutsulinkki vähintäänkin seuraavilla oikeuksilla
- Send Messages
- Embed Links
- Read Message History
- Add Reactions
- Connect
- Speak
- Priority Speaker (suositeltu)

### 2. Botin käyttöönotto
1. Suorita jar- tiedosto
2. jar- tiedoston käynistys luo samaan hakemistoon config.json tiedoston
3. Kopioi luomasi Discord-botin token ja liitä se config.json tiedostosta kohtaan "token".
4. Suorita jar-tiedosto uudestaan, ja botin pitäisi olla käyttö valmis.

### 3. Spotify ominaisuuden käyttöönotto
1. Luo uusi sovellus Spotifyn [web apiin](https://developer.spotify.com/)
2. Etsi luomasi sovelluksen Client ID ja Client secret ja kopioi ne samannimisiin kohtiin botin config.json tiedostoon.
3. Käynnistä botti uudestaan  

## käyttöohjeet
