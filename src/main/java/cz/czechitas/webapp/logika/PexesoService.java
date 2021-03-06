package cz.czechitas.webapp.logika;

import java.util.*;
import org.springframework.stereotype.*;
import cz.czechitas.webapp.entity.*;
import cz.czechitas.webapp.persistence.*;

@Component
public class PexesoService {

    private InMemoryPexesoRepository ulozisteHer;

    public PexesoService(InMemoryPexesoRepository ulozisteHer) {
        this.ulozisteHer = ulozisteHer;
    }

    public HerniPlocha vytvorNovouHerniPlochu() {
        List<Karta> karticky = new ArrayList<>();
        int cisloKarty = 0;
        for (int i = 0; i < 8; i++) {
            karticky.add(vytvorKartu(cisloKarty));
            cisloKarty++;
            karticky.add(vytvorKartu(cisloKarty));
            cisloKarty++;
        }
        Collections.shuffle(karticky);
        HerniPlocha novaPlocha = new HerniPlocha(karticky, StavHry.HRAC1_VYBER_PRVNI_KARTY);
        novaPlocha = ulozisteHer.save(novaPlocha);
        return novaPlocha;
    }

    private Karta vytvorKartu(int cisloKarty) {
        return new Karta(cisloKarty, StavKarty.RUBEM_NAHORU);
    }

    public HerniPlocha najdiHerniPlochu(Long id) {
        HerniPlocha aktualniPlocha = ulozisteHer.findById(id);
        return aktualniPlocha;
    }

    public void provedTah(Long idHerniPlochy, int poziceKartyNaKterouSeKliknulo) {
        HerniPlocha aktualniPlocha = ulozisteHer.findById(idHerniPlochy);

        if (aktualniPlocha.getStav() == StavHry.HRAC1_VYBER_PRVNI_KARTY) {
            vyberPrvniKartu(poziceKartyNaKterouSeKliknulo, aktualniPlocha);
        } else if (aktualniPlocha.getStav() == StavHry.HRAC1_VYBER_DRUHE_KARTY) {
            vyberDruhouKartu(poziceKartyNaKterouSeKliknulo, aktualniPlocha);
        } else if (aktualniPlocha.getStav() == StavHry.HRAC1_ZOBRAZENI_VYHODNOCENI) {
            List<Karta> karticky = vyhodnotOtoceneKarticky(aktualniPlocha);

            if (!jeKonecHry(karticky)) {
                aktualniPlocha.setStav(StavHry.HRAC1_VYBER_PRVNI_KARTY);
            } else {
                aktualniPlocha.setStav(StavHry.KONEC);
            }
        }

        ulozisteHer.save(aktualniPlocha);
    }

    private void vyberPrvniKartu(int poziceKartyNaKterouSeKliknulo, HerniPlocha aktualniPlocha) {
        Karta karticka = aktualniPlocha.getKarticky().get(poziceKartyNaKterouSeKliknulo);
        if (karticka.getStav() == StavKarty.RUBEM_NAHORU) {
            karticka.setStav(StavKarty.LICEM_NAHORU);
            aktualniPlocha.setStav(StavHry.HRAC1_VYBER_DRUHE_KARTY);
        }
    }

    private void vyberDruhouKartu(int poziceKartyNaKterouSeKliknulo, HerniPlocha aktualniPlocha) {
        Karta karticka = aktualniPlocha.getKarticky().get(poziceKartyNaKterouSeKliknulo);
        if (karticka.getStav() == StavKarty.RUBEM_NAHORU) {
            karticka.setStav(StavKarty.LICEM_NAHORU);
            aktualniPlocha.setStav(StavHry.HRAC1_ZOBRAZENI_VYHODNOCENI);

        }
    }

    private List<Karta> vyhodnotOtoceneKarticky(HerniPlocha aktualniPlocha) {
        List<Karta> karticky = aktualniPlocha.getKarticky();
        Karta karta1 = karticky.get(0);
        Karta karta2 = karticky.get(1);

        int i = 0;
        for (; i < karticky.size(); i++) {
            karta1 = karticky.get(i);
            if (karta1.getStav() == StavKarty.LICEM_NAHORU) break;
        }
        int j = i + 1;
        for (; j < karticky.size(); j++) {
            karta2 = karticky.get(j);
            if (karta2.getStav() == StavKarty.LICEM_NAHORU) break;
        }
        if (karta1.getCisloObrazku() == karta2.getCisloObrazku()) {
            karta1.setStav(StavKarty.ODEBRANA);
            karta2.setStav(StavKarty.ODEBRANA);
        } else {
            karta1.setStav(StavKarty.RUBEM_NAHORU);
            karta2.setStav(StavKarty.RUBEM_NAHORU);
        }
        return karticky;
    }

    private boolean jeKonecHry(List<Karta> karticky) {
        boolean jeKonec = true;
        for (Karta karta : karticky) {
            if (karta.getStav() != StavKarty.ODEBRANA) {
                jeKonec = false;
            }
        }
        return jeKonec;
    }
}

