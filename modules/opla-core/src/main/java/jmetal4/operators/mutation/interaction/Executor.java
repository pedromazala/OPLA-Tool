package jmetal4.operators.mutation.interaction;

import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Method;
import jmetal4.core.Solution;
import jmetal4.util.JMException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Executor {

    public static void interactionMutation(double probability, Solution solution) throws JMException {
        Mutation mutation = new Mutation(solution, probability);

        if (!mutation.satisfyProbability()) {
            return;
        }
        mutation.validateType();
        if (mutation.isEmpty()) {
            return;
        }

        List<Concern> concernsSelectedComp = mutation.getConcernsFromRandomClass();
        if (concernsSelectedComp.size() <= 1) {
            concernsSelectedComp.clear();
            return;
        }

        final Concern selectedConcern = mutation.getRandom(concernsSelectedComp);
        List<Class> allComponentsAssignedOnlyToConcern = new ArrayList<>(
                searchComponentsAssignedToConcern(selectedConcern, mutation.getAllComponents())
        );

        Class klass = mutation.getRandomAssignedClassOrNewOne(allComponentsAssignedOnlyToConcern);
        modularizeConcernInComponent(allComponents, klass, selectedConcern, mutation.getArchitecture());

        allComponentsAssignedOnlyToConcern.clear();
        concernsSelectedComp.clear();
    }

    private static List<Class> searchComponentsAssignedToConcern(Concern concern, List<Class> allComponents) {
        final List<Class> allComponentsAssignedToConcern = new ArrayList<>();

        for (Class klass : allComponents) {
            final Set<Concern> numberOfConcernsForPackage = getNumberOfConcernsFor(klass);
            if (numberOfConcernsForPackage.size() == 1 && (numberOfConcernsForPackage.contains(concern))) {
                allComponentsAssignedToConcern.add(klass);
            }
        }

        return allComponentsAssignedToConcern;
    }

    private static Set<Concern> getNumberOfConcernsFor(Class klass) {
        final Set<Concern> listOfOwnedConcern = new HashSet<>();

        for (Method method : klass.getAllMethods()) {
            listOfOwnedConcern.addAll(method.getOwnConcerns());
        }

        return listOfOwnedConcern;
    }
}
