package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.exception.NotFoundException;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface IcdRepository extends JpaRepository<Icd, UUID> {

    String MASK_NOT_FOUND = "Icd(id=%s) is not found";

    default Icd getOrThrow(UUID id) {
        if (id == null) {
            throw new NotFoundException(String.format(MASK_NOT_FOUND, id));
        }

        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }

    boolean existsByNameAndVersion(String name, Integer version);
}
