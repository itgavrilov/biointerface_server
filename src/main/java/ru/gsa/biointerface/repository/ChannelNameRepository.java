package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.NotFoundException;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ChannelNameRepository extends JpaRepository<ChannelName, UUID> {

    String MASK_NOT_FOUND = "ChannelName(id=%s) is not found";

    default ChannelName getOrThrow(UUID id) {
        if (id == null) {
            throw new NotFoundException(String.format(MASK_NOT_FOUND, id));
        }

        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }

    boolean existsByName(String name);
}
