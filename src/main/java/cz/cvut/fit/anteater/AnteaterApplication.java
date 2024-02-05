package cz.cvut.fit.anteater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import cz.cvut.fit.anteater.controller.BackgroundController;
import cz.cvut.fit.anteater.controller.ToolController;
import cz.cvut.fit.anteater.enumeration.Ability;
import cz.cvut.fit.anteater.model.entity.DndCharacter;
import cz.cvut.fit.anteater.repository.BackgroundRepository;
import cz.cvut.fit.anteater.repository.DndCharacterRepository;
import cz.cvut.fit.anteater.repository.DndClassRepository;
import cz.cvut.fit.anteater.repository.LanguageRepository;
import cz.cvut.fit.anteater.repository.RaceRepository;
import cz.cvut.fit.anteater.repository.SourceRepository;
import cz.cvut.fit.anteater.repository.ToolRepository;

@SpringBootApplication
public class AnteaterApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(AnteaterApplication.class, args);
	}

	@Autowired LanguageRepository languageRepository;
	@Autowired ToolRepository toolRepository;
	@Autowired DndClassRepository dndClassRepository;
	@Autowired BackgroundRepository backgroundRepository;
	@Autowired RaceRepository raceRepository;
	@Autowired ToolController toolController;
	@Autowired SourceRepository sourceRepository;
	//@Autowired BackgroundController backgroundController;
	@Autowired DndCharacterRepository charRepo;

	@Override
	public void run(String... args) throws Exception {
		DndCharacter c = charRepo.findAll().get(0);
		System.out.println(c.getAbility_scores().get(Ability.intelligence));
		System.out.println(c.getSkills());
		System.out.println(c);
	}
}
