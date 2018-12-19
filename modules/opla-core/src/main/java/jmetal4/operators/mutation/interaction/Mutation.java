package jmetal4.operators.mutation.interaction;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import jmetal4.core.Solution;
import jmetal4.core.Variable;
import jmetal4.problems.OPLA;
import jmetal4.util.Configuration;
import jmetal4.util.JMException;
import jmetal4.util.PseudoRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Mutation {

    private final Solution solution;
    private final Variable decisionVariable;
    private final Architecture architecture;
    private final List<Class> allComponents;
    private final double probability;

    private List<Concern> concernsFromRandomSelectedClass;

    public Mutation(Solution solution, double probability) {
        this.solution = solution;
        this.probability = probability;

        decisionVariable = solution.getDecisionVariables()[0];

        architecture = (Architecture) decisionVariable;
        allComponents = new ArrayList<>(architecture.getAllClasses());
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    public List<Class> getAllComponents() {
        return allComponents;
    }

    public boolean satisfyProbability() {
        return PseudoRandom.randDouble() < probability;
    }

    public void validateType() throws JMException {
        if (decisionVariable.getVariableType().toString().equals("class " + Architecture.ARCHITECTURE_TYPE)) {
            return;
        }

        Configuration.logger_.log(
                Level.SEVERE,
                "PLAFeatureMutation.featureInteractionMutation: invalid type. " + "{0}",
                decisionVariable.getVariableType()
        );
        throw new JMException("Unexpected");
    }

    public boolean isEmpty() {
        return allComponents.isEmpty();
    }

    public Class getRandom() {
        return getRandom(allComponents);
    }

    public <T> T getRandom(List<T> allComponents) {
        int numObjects = allComponents.size();
        if (numObjects == 0) {
            return null;
        }

        int key = PseudoRandom.randInt(0, numObjects - 1);
        return allComponents.get(key);
    }

    public List<Concern> getConcernsFromRandomClass() {
        if (concernsFromRandomSelectedClass == null) {
            initConcernsFromRandomClass();
        }

        return concernsFromRandomSelectedClass;
    }

    private void initConcernsFromRandomClass() {
        Class selectedComp = getRandom();
        concernsFromRandomSelectedClass = new ArrayList<>(selectedComp.getAllConcerns());
    }

    public Class getRandomAssignedClassOrNewOne(List<Class> allComponentsAssignedOnlyToConcern) {
        if (!allComponentsAssignedOnlyToConcern.isEmpty()) {
            return getRandom(allComponentsAssignedOnlyToConcern);
        }

        OPLA.contComp_++;
        String klassName = "Class" + OPLA.contComp_;

        return architecture.createClass(klassName, false);
    }
}
