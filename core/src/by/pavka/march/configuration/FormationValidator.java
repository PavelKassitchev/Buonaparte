package by.pavka.march.configuration;

import by.pavka.march.military.Force;

public interface FormationValidator {
    boolean canAttach(Force force);
}
