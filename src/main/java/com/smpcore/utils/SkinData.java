package com.smpcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public enum SkinData {

    RTP_NPC(
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMzQ3NTA1MzY4MywKICAicHJvZmlsZUlkIiA6ICI2ZGM3NDNjNmUxNjg0MmFhOWIzN2VmYzUxNGI1MDJmYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbmRlcnNwZWFybDE4NCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNjFjY2IwYTYyZWU5Y2ExNzE0ZTY5Y2ZiNzUwZmRmMDY3ZmNhOWI1YTFkZjVlNGEyNWY1NTQ2ODA2YWE4MmU2IgogICAgfQogIH0KfQ==",
            "RBIgOnLS9iEk/7AMojIuOMz654lV7sLUjvJMj1Smqkd/pqnMm3m9q+wEOzSc/DIq1TSLF8hiXd0ZU0HTCqusfFsJmAzcNMKlWBwpiidmh3+KutNR5GW7tCi9NUS87V/rpPgjrjalkJ2SO83SCmCgJezPxg8Z8j42QqE9n5XqEm/q4CpAjmpTIr//G2iDmvtZTgBvkiq0XoJq8TDeMMFrAjrwmg2D3DxOVZiZqeNRYSr9CgBL73v4U2pisbB/ASBjHmoK96kd1eGJVL0pPoMkYLBvTe1fAl1PKtSYEEinN5K1m+ZfwVp1c7Vz136KUg8hcP1CSB3WbDb2OphIqxNdKcw5j5recf/716FnrBgxtdc4GIzmCKezfERoJhksu9JnQbfX90hXyMbFi/w8xvsCPqdAFB4yDdosdD+R57bUOy2UI8j2uh6YydbRa786NJr3bOMhT0nXpDAxflGdiRvwzBL96e31EL9/Wg9DDWetQbt8xPFF57aoHKQaEYgYb8efQE44PXiJ0GAWI83XIpSVl7i0IU1Jm0t5rSpYQUPfqLIs115emDgHBsnQe52Dhr70aagQdlpmsiPW/BGaFs5yaAeKdPbktK//Mp2hKYMvaXTbt8qiT5NLVXilQ2+83IMkbrRHU7PVni8qV+xLAJdhPuo1bkFMqQkNRX4BsKgY68I="
    ),

    CASINO_NPC(
            "ewogICJ0aW1lc3RhbXAiIDogMTc2OTQyMzMyNTk1MywKICAicHJvZmlsZUlkIiA6ICI2NGRiNmMwNTliOTk0OTM2YTY0M2QwODEwODE0ZmJkMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVTaWx2ZXJEcmVhbXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJiMzU1ZTdkZWE5MWFmNTA2OTdjM2RmZGJmYWYxM2ZiNWY2YTg1YTFkNTFiMWIxNTc1YjNjYzg1ODBkMTIxYiIKICAgIH0KICB9Cn0=",
            "dlc8oUdErby0gTeeGXNi5kgcvyZ7U5Dlm3ZLQ4egh/bg3ie9i7tWeArGT0a+Lc9W01apE5ZY1H5sSD2T2z/rZtOrDqCWedk+uIpyRqR8Xr1MuwYvt6kHfeJ3KP0LBG8Q2Nxyliv9bdycpH8plydopZh3X3GA+n5LsZFIGA1a+1IKWstJMgwlPAKxi387/WyyldPOb4aH4qE7uJip1iI/9JxGmzkXJ0+jKE9nRFx22ZNQ+qcQUAQCXxayLWmOotJ99+90G1E1O5jY1etR9z4BNVO0JhDN5ukwCM1dfMc/kBnlYDP6yE0sF4Sh1oZgFN7E2Hlg9cLv4LPY72aIjRLPFwsOEydrqKy/zuz1GwqMzV1rqfnbrAV6CKYeWVStXEKT54f0CIwkxYtVZfBjHRE/+ZAMbRE0aJTQEC5JkQ0cu3Tr63R1DF61tpxJO4fOsIj6xQ7RT84xD/KpraZNVcBU8in1CXpVxidOqqv9ZEtPpJcf8m39CRaDv1HyhEiAq9iuZbJa5ORLnIDrQnEDt5y0ne++R6GsOZ8D5I+ZxGnoFauEk4DNCzXMzcfyUq9VRVPQAyN60JfKc66B62yBPCjJ15rZl7vG2x2mavaDU82HRBZO5AMZlriy7xgGpi5QQ6RVxk1M3jfn02BH/wRKBcWwuK4wgVGNS6aawVKlSa8c61k="
    ),

    AUCTION_NPC(
            "ewogICJ0aW1lc3RhbXAiIDogMTcxMzU4Mjg3MDI5MSwKICAicHJvZmlsZUlkIiA6ICIzMjNiYjlkYzkwZWU0Nzk5YjUxYzE3NjRmZDRhNjI3OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOcGllIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkNzk3NDFhYjQ4MjhjYzM5MzI3YmUyOTM5ZTFiM2UyNjRlZTE0ZWYzMmUwMjYzMGNhZWU0Nzc3MjVlYTE2OGYiCiAgICB9CiAgfQp9",
            "W1Iokie8a41ENYlhnAVHriEySwjSjnqqSWF2eDhGLroA9zYUdEL94AwAddbBOYSKID+16l9CeBAFzxlwDbqsSYI0czHXbaPgaZZMph/mbXJAJIJW7B6Ywh6sZoAZP6D7qLM0mimtIehle7ocKNDHWrj9L7sC0PFcgBV6FRDknLl2ip/pcmcuE5VXDR+RUMrDi/sMeBINWwXhuM96wie99DaEmeNI3HtNlQ6myyUwFN3/Gz0IGa2cG1L2MnEAvuJgt7CqdPmBWCJ8f/BOhIN3kuRKWsTzpeHcwIDhPwD+d0DJJD0ZVy7DibAeVOj1S0s396EC5jdgdAe65HQP2S0MOGnVON8gDI59eQ1CVYnc8mTQbozLxSE6FxAJBKJARL6mg/lzIq0O/CImuDAeLDOO+2TSc2lA2FGbmphwtRk2I+9LvUgwKaDBQBB6quXUvv6zIAWbli9MaOn8TCp2kNbND+MXT4XqeBFzYxlVROT/IAYncaTuI17PGD/jmUkaFq9k/70mcQ0we85qkZ17hLqM/U1IDaVDPRysEcLhgOTo4zJdR9uc1BJbZbdM1kCXqEq+k402D5eqfMrkNLi5ESBtT+J5n/las+saOrcec39XHvNdDTKQy58W719FbK7cjncogUrgpUfpEvYTvRzLUCXhzs63B9cpReREWUldnpkH0F0="
    ),

    JEWELER_NPC(
            "ewogICJ0aW1lc3RhbXAiIDogMTcxMzU4Mjg3MDI5MSwKICAicHJvZmlsZUlkIiA6ICIzMjNiYjlkYzkwZWU0Nzk5YjUxYzE3NjRmZDRhNjI3OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOcGllIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkNzk3NDFhYjQ4MjhjYzM5MzI3YmUyOTM5ZTFiM2UyNjRlZTE0ZWYzMmUwMjYzMGNhZWU0Nzc3MjVlYTE2OGYiCiAgICB9CiAgfQp9",
            "W1Iokie8a41ENYlhnAVHriEySwjSjnqqSWF2eDhGLroA9zYUdEL94AwAddbBOYSKID+16l9CeBAFzxlwDbqsSYI0czHXbaPgaZZMph/mbXJAJIJW7B6Ywh6sZoAZP6D7qLM0mimtIehle7ocKNDHWrj9L7sC0PFcgBV6FRDknLl2ip/pcmcuE5VXDR+RUMrDi/sMeBINWwXhuM96wie99DaEmeNI3HtNlQ6myyUwFN3/Gz0IGa2cG1L2MnEAvuJgt7CqdPmBWCJ8f/BOhIN3kuRKWsTzpeHcwIDhPwD+d0DJJD0ZVy7DibAeVOj1S0s396EC5jdgdAe65HQP2S0MOGnVON8gDI59eQ1CVYnc8mTQbozLxSE6FxAJBKJARL6mg/lzIq0O/CImuDAeLDOO+2TSc2lA2FGbmphwtRk2I+9LvUgwKaDBQBB6quXUvv6zIAWbli9MaOn8TCp2kNbND+MXT4XqeBFzYxlVROT/IAYncaTuI17PGD/jmUkaFq9k/70mcQ0we85qkZ17hLqM/U1IDaVDPRysEcLhgOTo4zJdR9uc1BJbZbdM1kCXqEq+k402D5eqfMrkNLi5ESBtT+J5n/las+saOrcec39XHvNdDTKQy58W719FbK7cjncogUrgpUfpEvYTvRzLUCXhzs63B9cpReREWUldnpkH0F0="
    ),

    GINGERBREAD_NPC(
            "ewogICJ0aW1lc3RhbXAiIDogMTc3MDQxNzgyMDg2OCwKICAicHJvZmlsZUlkIiA6ICI3MzFiOTdlYTI1MWM0ZjNmYTk0OTEwY2RkMmQwOTU4YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJJbU5vdEFDYXQ2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkzYzQ3NGViNTNiZGYzNzhhODdkYzQyZmZjOGIyNzYyNGVlMzliMmMxYjVkYzEzYmEyZWJmMjRiYjExYzE3ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp",
            "oRq0IlBYsZWAOQBSC6euAuZPOmzzNvLpQVTnG/eZZjM56qw6LdSVijUka0rsmxqIO7NT79IgfNiKBBgUtMu3RFMmnQKWVA7QUoDkkVHKTsBQnjX/j82o6/m4mzCWoNTL7/gN0vPIb4r2DMVRxKqo7XjwM/SMGE7BZnLVV8kDICbBWFcil40hzrt5t0g8bLuAjYrj0dcOKfSmZrF8gvxXEg093FOCLySKY6Ma/aUc7AOXKecjGBrRAQwazTJz1+3McvYLwnEl3N8By1lrBjRVPHuaxWSzOC9o7MvnkUQ2Rngf5G9c7gH59gVFb6ewdhya7G4LelQyKpaQSemlKfBWaGeyiovU+ZodDwH0bRJaCAxyK0I2VWZsTKXDWz2d9oPhPa2MFGS5hVLhfcHXFQuqTFnM8TGKOlGkxiMASxgfly7yahWKeYpTWIp1l9QTWTI1qBlzmUyS9wpJHyiinzxO2ed6NNAkOJGENw1y1YfuTDV7bywa6BwtUljkTmA7ZsVXxyvezHVT13qGRDb9Mzn0qgb9s+04BWiP1ANC8txAirn9I8eGqBSZloKdZ9E3iUsPZeqCI5MV48xLr4X9ASljIA+1Yn7pO+4QAKtCWLehqRAB6tXx0WK66LAm9fMSAv4Szn6ZEqHG5l85BhvPSYf541XW/SZiM60CKsbH/SowHdU="
    );

    private final String value;
    private final String signature;

    SkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() { return value; }
    public String getSignature() { return signature; }

    public PlayerProfile getProfile() {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            String json = new String(Base64.getDecoder().decode(this.value));
            int urlStartIndex = json.indexOf("http://textures.minecraft.net/texture/");

            if (urlStartIndex != -1) {
                int urlEndIndex = json.indexOf("\"", urlStartIndex);
                String urlStr = json.substring(urlStartIndex, urlEndIndex);

                textures.setSkin(new URL(urlStr));
                profile.setTextures(textures);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return profile;
    }
}