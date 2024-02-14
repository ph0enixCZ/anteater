package cz.cvut.fit.anteater.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import cz.cvut.fit.anteater.model.entity.Source;
import cz.cvut.fit.anteater.model.entity.SourceableEntity;
import cz.cvut.fit.anteater.repository.BaseRepository;
import cz.cvut.fit.anteater.repository.SourceRepository;

public abstract class BaseService<T extends SourceableEntity> {
	private BaseRepository<T> repository;
	private SourceRepository sourceRepository;

	public BaseService(BaseRepository<T> repository, SourceRepository sourceRepository) {
		this.repository = repository;
		this.sourceRepository = sourceRepository;
	}

	public Long count() {
		return repository.count();
	}

	public List<T> findAll() {
		return repository.findAll();
	}

	public T findById(String id) {
		if (id == null) throw new IllegalArgumentException("ID cannot be null");
		return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Entity with given ID not found"));
	}

	public List<T> findByName(String name) {
		return repository.findByName(name);
	}

	private List<Source> convertSources(List<String> sourceIds) {
		List<Source> sourceList = new ArrayList<>();
		for (String source : sourceIds) {
			sourceList.add(sourceRepository.findById(source).orElseThrow(() -> new IllegalArgumentException("Source with given ID not found")));
		}
		return sourceList;
	}

	public List<T> findBySourceList(List<String> sourceIds) {
		if (sourceIds.isEmpty()) return findAll();
		return repository.findBySourceIn(convertSources(sourceIds));
	}

	public List<T> findByNameAndSources(String name, List<String> sourceIds) {
		if (name == null && sourceIds == null) return findAll();
		if (name == null) return findBySourceList(sourceIds);
		if (sourceIds == null) return findByName(name);
		return repository.findByNameAndSourceIn(name, convertSources(sourceIds));
	}

	public T save(T entity, Boolean isUpdate) {
		if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
		if (isUpdate != repository.existsById(entity.getId())) throw new IllegalArgumentException("Invalid create/update operation");
		return repository.save(entity);
	}

	public void delete(String id) {
		if (id == null) throw new IllegalArgumentException("ID cannot be null");
		if (!repository.existsById(id)) throw new NoSuchElementException("Entity with given ID not found");
		repository.deleteById(id);
	}
}
