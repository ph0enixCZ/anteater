package cz.cvut.fit.anteater.service;

import org.springframework.stereotype.Service;

import cz.cvut.fit.anteater.entity.DndClass;
import cz.cvut.fit.anteater.repository.DndClassRepository;

@Service
public class DndClassService extends BaseService<DndClass> {
	public DndClassService(DndClassRepository repository) {
		super(repository);
	}
}
