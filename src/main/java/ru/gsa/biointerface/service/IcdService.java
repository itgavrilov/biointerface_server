package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.dto.IcdDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * CRUD-сервис для работы с заболеваниями по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IcdService {
    private final IcdRepository repository;

    @PostConstruct
    private void init() {
        log.info("IcdService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("IcdService is destruction");
    }

    /**
     * Получение списка заболиваний
     *
     * @return Список заболиваний {@link List<Icd>}
     */
    public List<Icd> findAll() {
        return repository.findAll();
    }

    /**
     * Получение списка заболиваний с пагинацией
     *
     * @param pageable Пагинация {@link Pageable}
     * @return Список устройств с пагинацией {@link Page<Icd>}
     */
    public Page<Icd> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Получение заболиваний по id
     *
     * @param id Идентификатор {@link Icd#getId()}
     * @return Заболивание {@link Icd}
     * @throws NotFoundException если заболивание с id не найдено
     */
    public Icd getById(Integer id) {
        return repository.getOrThrow(id);
    }

    /**
     * Создание/обновление заболивания
     *
     * @param dto DTO заболивания {@link IcdDTO}
     * @return Заболивание {@link Icd}
     */
    @Transactional
    public Icd save(IcdDTO dto) {
        Optional<Icd> optional = repository.findById(dto.getId());
        Icd entity;

        if (optional.isEmpty()) {
            entity = new Icd(dto.getName(), dto.getVersion(), dto.getComment());
        } else {
            entity = optional.get();
            entity.setName(dto.getName());
            entity.setVersion(dto.getVersion());
            entity.setComment(dto.getComment());
        }

        entity = repository.save(entity);
        log.debug("Icd(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Удаление заболивания
     *
     * @param id Идентификатор {@link Icd#getId()}
     * @throws NotFoundException если заболивание с id не найдено
     */
    @Transactional
    public void delete(Integer id) {
        Icd entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.debug("Icd(id={}) is deleted", id);
    }
}
