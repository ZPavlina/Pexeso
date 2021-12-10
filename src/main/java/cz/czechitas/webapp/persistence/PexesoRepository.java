package cz.czechitas.webapp.persistence;

import cz.czechitas.webapp.entity.*;

public interface PexesoRepository {

    HerniPlocha findById(Long id);

    HerniPlocha save(HerniPlocha plocha);


}
