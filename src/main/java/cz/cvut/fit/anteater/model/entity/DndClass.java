package cz.cvut.fit.anteater.model.entity;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Field;

import cz.cvut.fit.anteater.enumeration.Ability;
import cz.cvut.fit.anteater.enumeration.ArmorType;
import cz.cvut.fit.anteater.enumeration.Skill;
import cz.cvut.fit.anteater.enumeration.WeaponType;
import cz.cvut.fit.anteater.model.value.BonusList;
import cz.cvut.fit.anteater.model.value.Dice;
import cz.cvut.fit.anteater.model.value.Spellcasting;
import cz.cvut.fit.anteater.model.value.TextFeature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DndClass extends SourceableEntity {

	private String description;

	private Dice hitDice;

	private List<TextFeature> features;

	private List<String> subclasses;

	@Field("skills")
	private BonusList<Skill> skillProficiencies;

	@Field("saves")
	private Set<Ability> savingThrowProficiencies;

	@Field("tools")
	private BonusList<Tool> toolProficiencies;

	@Field("armor_types")
	private Set<ArmorType> armorProficiencyTypes;

	@Field("armor")
	private List<Armor> armorProficiencies;

	@Field("weapon_types")
	private Set<WeaponType> weaponProficiencyTypes;

	@Field("weapons")
	private List<Weapon> weaponProficiencies;

	private Spellcasting spellcasting;
}
