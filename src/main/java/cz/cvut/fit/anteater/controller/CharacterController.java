package cz.cvut.fit.anteater.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.cvut.fit.anteater.business.CharacterService;
import cz.cvut.fit.anteater.constants.Constants;
import cz.cvut.fit.anteater.dto.request.CharacterInput;
import cz.cvut.fit.anteater.dto.request.IdWrapper;
import cz.cvut.fit.anteater.dto.request.SkillInput;
import cz.cvut.fit.anteater.dto.response.AttackOutput;
import cz.cvut.fit.anteater.dto.response.CharacterComplete;
import cz.cvut.fit.anteater.dto.response.CharacterShort;
import cz.cvut.fit.anteater.dto.response.SkillOutput;
import cz.cvut.fit.anteater.model.entity.Armor;
import cz.cvut.fit.anteater.model.entity.Spell;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for handling requests related to {@link Character} entities. Provides
 * CRUD functionality for characters, as well as more specific endpoints for editing
 * parts of the character and generating a PDF character sheet using the character's data.
 * 
 * @see Character
 */
@CrossOrigin(origins = Constants.FRONTEND_URL)
@RestController
@RequestMapping(Constants.BASE_API_URL + "characters")
public class CharacterController {
	private CharacterService characterService;

	public CharacterController(CharacterService characterService) {
		this.characterService = characterService;
	}

	@Operation(summary = "Get basic information about all characters",
		description = "Get a list of all characters containing only their basic information.")
	@ApiResponse(responseCode = "200", description = "All characters found and returned.")
	@GetMapping
	public List<CharacterShort> getAll() {
		return characterService.getAllCharacters();
	}

	@Operation(summary = "Get complete information about a character by its ID",
		description = "Get a single character with all its information, including character stats, skills, weapons " +
		"armor, spells, proficiencies and their related outputs. The character to get is first identified by its ID.")
	@ApiResponse(responseCode = "200", description = "Character found and returned.")
	@ApiResponse(responseCode = "404", description = "Character with given ID not found.")
	@GetMapping("/{id}")
	public CharacterComplete getCompleteById(@PathVariable @Parameter(description = "ID of character to return") String id) {
		return characterService.getCompleteCharacter(id);
	}

	@Operation(summary = "Create a new character",
		description = "Create a new character with the provided data. The character is saved and returned. See schema for the expected input format.")
	@ApiResponse(responseCode = "200", description = "Character created and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data.")
	@PostMapping
	public CharacterComplete createCharacter(@RequestBody @Valid @Parameter(description = "The character choices to " +
		"create the character from. At minimum, the input must contain IDs of the chosen class, race, background, tools, " +
		"languages and sources, as well as ability score data. The character ID must be null, as it is " +
		"generated by the system.") CharacterInput entity) {
		try {
			if (entity.getId() != null) throw new IllegalArgumentException("ID must be null");
			return characterService.saveCharacter(entity, true);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@Operation(summary = "Rebuild an existing character",
		description = "Rebuild an existing character using new choices, but keep the character's ID instead of creating a " +
		"new one. See schema for the expected input format.")
	@ApiResponse(responseCode = "200", description = "Character updated and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data.")
	@ApiResponse(responseCode = "404", description = "Character with given ID not found.")
	@PutMapping("/{id}")
	public CharacterComplete updateCharacter(
			@PathVariable @Parameter(description = "The ID of the character to update, must match the ID in the request body") String id,
			@RequestBody @Valid @Parameter(description = "The new character choices to rebuild the character from. The ID must " +
			"match the ID in the path.") CharacterInput entity) {
		try {
			if (entity.getId() == null) throw new IllegalArgumentException("ID cannot be null");
			if (!Objects.equals(id, entity.getId())) throw new IllegalArgumentException("ID in path and body must match");
			return characterService.saveCharacter(entity, false);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Operation(summary = "Delete a character by its ID",
		description = "Delete a character by its ID. The character is permanently removed from the system.")
	@ApiResponse(responseCode = "200", description = "Character deleted.")
	@ApiResponse(responseCode = "404", description = "Character with given ID not found.")
	@DeleteMapping("/{id}")
	public String deleteCharacter(@PathVariable @Parameter(description = "ID of character to delete") String id) {
		try {
			characterService.deleteCharacter(id);
			return "Character deleted";
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Operation(summary = "Edit the skills of a character",
		description = "Edit the skills of a character by providing a new list of skills the character has proficiency " +
		"in. The new list of skills is saved and a list of all skills with their new proficiency status and " +
		"calculated modifiers is returned. See schema for the expected input format.")
	@ApiResponse(responseCode = "200", description = "Skills edited and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data.")
	@PutMapping("/{id}/skills")
	public List<SkillOutput> editSkills(
			@PathVariable @Parameter(description = "The ID of the character to edit") String id,
			@RequestBody @Valid @Parameter(description = "The list of skills and their proficiency flags to set. If a skill " +
			"is not sent, its proficiency value is assumed to be false.") List<SkillInput> skills) {
		return characterService.editSkills(id, skills);
	}

	@Operation(summary = "Edit the weapons of a character",
		description = "Edit the weapons of a character by providing a new list of weapon IDs the character is equipped with. " +
		"The new list is saved and returned with calculated attack outputs for each weapon.")
	@ApiResponse(responseCode = "200", description = "Weapons edited and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data.")
	@PutMapping("/{id}/weapons")
	public List<AttackOutput> editWeapons(
			@PathVariable @Parameter(description = "The ID of the character to edit") String id,
			@RequestBody @Parameter(description = "The list of weapon IDs to set") List<String> weaponIds) {
		return characterService.editWeapons(id, weaponIds);
	}

	@Operation(summary = "Edit the armor of a character",
		description = "Edit the armor of a character by providing a new armor ID the character is equipped with. " +
		"The new armor is saved and returned. If an empty string is given, the character will be unarmored. " + 
		"The character's new armor class can be obtained by getting the character information again.")
	@ApiResponse(responseCode = "200", description = "Armor edited and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data.")
	@PutMapping("/{id}/armor")
	public Armor editArmor(
			@PathVariable @Parameter(description = "The ID of the character to edit") String id,
			@RequestBody @Valid @Parameter(description = "The armor ID to set wrapped in an object with " + 
			"an 'id' field.") IdWrapper armorId) {
		return characterService.editArmor(id, armorId.getId());
	}

	@Operation(summary = "Edit the spells of a character",
		description = "Edit the spells of a character by providing a new list of spell IDs the character knows. " +
		"The new list is saved and returned. Must be used with a character that has spellcasting abilities.")
	@ApiResponse(responseCode = "200", description = "Spells edited and returned.")
	@ApiResponse(responseCode = "400", description = "Invalid input data or character does not have spellcasting abilities.")
	@PutMapping("/{id}/spells")
	public List<Spell> editSpells(
			@PathVariable @Parameter(description = "The ID of the character to edit") String id,
			@RequestBody @Parameter(description = "The list of spell IDs to set") List<String> spellIds) {
		return characterService.editSpells(id, spellIds);
	}

	@Operation(summary = "Level up a character",
		description = "Level up a character by increasing its level by one. The character's stats may change " +
		"or new features may be added. Full character information is returned after the level up. Can only be used " +
		"if the character has not reached the maximum level in D&D (20).")
	@ApiResponse(responseCode = "200", description = "Character leveled up and returned.")
	@ApiResponse(responseCode = "400", description = "Character has reached the maximum level.")
	@ApiResponse(responseCode = "404", description = "Character with given ID not found.")
	@PostMapping("/{id}/levelup")
	public CharacterComplete levelUp(@PathVariable @Parameter(description = "The ID of the character to level up") String id) {
		return characterService.levelUp(id);
	}

	@Operation(summary = "Generate a PDF character sheet",
		description = "Generate a PDF character sheet using the character's data. The PDF is returned as a downloadable " +
		"file. The character to generate the PDF for is first identified by its ID.")
	@ApiResponse(responseCode = "200", description = "PDF generated and returned.")
	@ApiResponse(responseCode = "404", description = "Character with given ID not found.")
	@GetMapping("/{id}/pdf")
	public ResponseEntity<Resource> getPdf(@PathVariable @Parameter(description = "The ID of character to generate PDF for") String id) {
		Resource pdf = characterService.getPdf(id);
		return ResponseEntity.ok()
			.header("Content-Disposition", "attachment; filename=\"" + pdf.getFilename() + "\"")
			.body(pdf);
	}
}
