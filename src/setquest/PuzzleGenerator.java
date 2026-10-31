package setquest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// construye y administra el banco de preguntas
public class PuzzleGenerator {

    private final List<Puzzle> regularPool = new ArrayList<>();
    private final List<Puzzle> breakerPool = new ArrayList<>();
    private final Random random = new Random();

    public PuzzleGenerator() {
        buildBank();
        Collections.shuffle(regularPool);
        Collections.shuffle(breakerPool);
    }

    // entrega una pregunta sin repetir dentro de la partida
    public Puzzle nextPuzzle(boolean breaker) {
        if (breaker && !breakerPool.isEmpty()) {
            return breakerPool.remove(breakerPool.size() - 1);
        }

        if (!regularPool.isEmpty()) {
            return regularPool.remove(regularPool.size() - 1);
        }

        if (!breakerPool.isEmpty()) {
            return breakerPool.remove(breakerPool.size() - 1);
        }

        throw new IllegalStateException("no quedan preguntas disponibles");
    }

    public int getTotalBankSize() {
        return regularPool.size() + breakerPool.size();
    }

    public int getRegularCount() {
        return regularPool.size();
    }

    public int getBreakerCount() {
        return breakerPool.size();
    }

    // crea las 90 preguntas del banco
    private void buildBank() {
        addTruthValuePuzzles();
        addConceptPuzzles();
        addQuantifierPuzzles();
        addSetPuzzles();
        addBreakerPuzzles();

        if (regularPool.size() + breakerPool.size() != 90) {
            throw new IllegalStateException("el banco debe contener 90 preguntas");
        }
    }

    // crea 40 preguntas de evaluacion proposicional
    private void addTruthValuePuzzles() {
        TruthTemplate[] templates = {
            new TruthTemplate("P ∧ Q", (p, q, r) -> p && q),
            new TruthTemplate("P ∨ Q", (p, q, r) -> p || q),
            new TruthTemplate("¬P ∧ Q", (p, q, r) -> !p && q),
            new TruthTemplate("P → Q", (p, q, r) -> !p || q),
            new TruthTemplate("P ↔ Q", (p, q, r) -> p == q),
            new TruthTemplate("(P ∧ Q) → R", (p, q, r) -> !(p && q) || r),
            new TruthTemplate("P ∨ (Q ∧ ¬R)", (p, q, r) -> p || (q && !r)),
            new TruthTemplate("¬(P ∨ Q) ∨ R", (p, q, r) -> !(p || q) || r),
            new TruthTemplate("(P → Q) ∧ (Q → R)", (p, q, r) -> (!p || q) && (!q || r)),
            new TruthTemplate("(P ↔ Q) ∨ ¬R", (p, q, r) -> p == q || !r)
        };

        boolean[][] assignments = {
            {true, false, true},
            {false, true, false},
            {true, true, false},
            {false, false, true}
        };

        for (TruthTemplate template : templates) {
            for (boolean[] values : assignments) {
                boolean result = template.expression.evaluate(values[0], values[1], values[2]);
                String question = """
                        sea p = %s, q = %s y r = %s.

                        cual es el valor de:
                        %s
                        """.formatted(
                        truthWord(values[0]),
                        truthWord(values[1]),
                        truthWord(values[2]),
                        template.text
                );

                addRegular(
                        question,
                        booleanOptions(result),
                        result ? "verdadero" : "falso",
                        "logica proposicional",
                        "rapida",
                        11
                );
            }
        }
    }

    // crea 12 preguntas de conceptos y reglas logicas
    private void addConceptPuzzles() {
        addRegular(
                "cual expresion es equivalente a P → Q?",
                new String[]{"¬P ∨ Q", "P ∧ Q", "P ∨ ¬Q", "¬P ∧ Q"},
                "¬P ∨ Q", "equivalencias", "media", 13
        );

        addRegular(
                "cual es la contrapositiva de P → Q?",
                new String[]{"¬Q → ¬P", "Q → P", "¬P → ¬Q", "P ∨ Q"},
                "¬Q → ¬P", "implicaciones", "media", 13
        );

        addRegular(
                "cual es la conversa de P → Q?",
                new String[]{"Q → P", "¬Q → ¬P", "¬P → ¬Q", "P ↔ Q"},
                "Q → P", "implicaciones", "media", 13
        );

        addRegular(
                "aplica de morgan a ¬(P ∧ Q).",
                new String[]{"¬P ∨ ¬Q", "¬P ∧ ¬Q", "P ∨ Q", "P ∧ Q"},
                "¬P ∨ ¬Q", "equivalencias", "media", 13
        );

        addRegular(
                "aplica de morgan a ¬(P ∨ Q).",
                new String[]{"¬P ∧ ¬Q", "¬P ∨ ¬Q", "P ∧ Q", "P ∨ Q"},
                "¬P ∧ ¬Q", "equivalencias", "media", 13
        );

        addRegular(
                "que tipo de formula es P ∨ ¬P?",
                new String[]{"tautologia", "contradiccion", "contingencia", "implicacion"},
                "tautologia", "clasificacion", "media", 12
        );

        addRegular(
                "que tipo de formula es P ∧ ¬P?",
                new String[]{"contradiccion", "tautologia", "contingencia", "bicondicional"},
                "contradiccion", "clasificacion", "media", 12
        );

        addRegular(
                "premisas: P → Q y P. cual conclusion sigue por modus ponens?",
                new String[]{"Q", "¬Q", "¬P", "P ↔ Q"},
                "Q", "argumentos", "media", 14
        );

        addRegular(
                "premisas: P → Q y ¬Q. cual conclusion sigue por modus tollens?",
                new String[]{"¬P", "P", "Q", "P ∧ Q"},
                "¬P", "argumentos", "media", 14
        );

        addRegular(
                "premisas: P → Q y Q → R. cual implicacion se obtiene?",
                new String[]{"P → R", "R → P", "Q → P", "P → ¬R"},
                "P → R", "argumentos", "media", 14
        );

        addRegular(
                "que significa P ↔ Q?",
                new String[]{
                    "P y Q tienen el mismo valor de verdad",
                    "P siempre es verdadero",
                    "Q siempre es falso",
                    "P implica Q pero Q no implica P"
                },
                "P y Q tienen el mismo valor de verdad", "bicondicional", "media", 14
        );

        addRegular(
                "en cual caso P → Q es falso?",
                new String[]{
                    "P verdadero y Q falso",
                    "P falso y Q verdadero",
                    "P falso y Q falso",
                    "P verdadero y Q verdadero"
                },
                "P verdadero y Q falso", "implicaciones", "media", 14
        );
    }

    // crea 12 preguntas con cuantificadores
    private void addQuantifierPuzzles() {
        addRegular(
                "cual es la negacion de ∀x P(x)?",
                new String[]{"∃x ¬P(x)", "∀x ¬P(x)", "∃x P(x)", "¬∃x ¬P(x)"},
                "∃x ¬P(x)", "cuantificadores", "media", 15
        );

        addRegular(
                "cual es la negacion de ∃x P(x)?",
                new String[]{"∀x ¬P(x)", "∃x ¬P(x)", "∀x P(x)", "¬∀x ¬P(x)"},
                "∀x ¬P(x)", "cuantificadores", "media", 15
        );

        addRegular(
                "cual formula representa: todos los alumnos estudian?",
                new String[]{
                    "∀x(alumno(x) → estudia(x))",
                    "∃x(alumno(x) ∧ estudia(x))",
                    "∀x(alumno(x) ∧ estudia(x))",
                    "∃x(alumno(x) → estudia(x))"
                },
                "∀x(alumno(x) → estudia(x))", "cuantificadores", "media", 16
        );

        addRegular(
                "cual formula representa: existe un alumno que no estudia?",
                new String[]{
                    "∃x(alumno(x) ∧ ¬estudia(x))",
                    "∀x(alumno(x) → ¬estudia(x))",
                    "¬∃x(alumno(x) ∧ ¬estudia(x))",
                    "∀x(alumno(x) ∧ estudia(x))"
                },
                "∃x(alumno(x) ∧ ¬estudia(x))", "cuantificadores", "media", 16
        );

        addRegular(
                "dominio D = {1, 2, 3, 4}. cual es el valor de ∀x(x > 0)?",
                booleanOptions(true), "verdadero", "cuantificadores", "media", 14
        );

        addRegular(
                "dominio D = {1, 2, 3, 4}. cual es el valor de ∃x(x es par ∧ x > 3)?",
                booleanOptions(true), "verdadero", "cuantificadores", "media", 14
        );

        addRegular(
                "dominio D = {1, 2, 3, 4}. cual es el valor de ∀x(x es par)?",
                booleanOptions(false), "falso", "cuantificadores", "media", 14
        );

        addRegular(
                "dominio D = {-3, -2, -1, 0, 1, 2}. cual es el valor de ∃x(x² = 9)?",
                booleanOptions(true), "verdadero", "cuantificadores", "media", 15
        );

        addRegular(
                "cual formula expresa A ⊆ B?",
                new String[]{
                    "∀x(x ∈ A → x ∈ B)",
                    "∃x(x ∈ A ∧ x ∈ B)",
                    "∀x(x ∈ B → x ∈ A)",
                    "∃x(x ∈ A ∧ x ∉ B)"
                },
                "∀x(x ∈ A → x ∈ B)", "cuantificadores y conjuntos", "media", 17
        );

        addRegular(
                "cual formula expresa A ⊈ B?",
                new String[]{
                    "∃x(x ∈ A ∧ x ∉ B)",
                    "∀x(x ∈ A → x ∈ B)",
                    "∃x(x ∈ B ∧ x ∉ A)",
                    "∀x(x ∉ A → x ∈ B)"
                },
                "∃x(x ∈ A ∧ x ∉ B)", "cuantificadores y conjuntos", "media", 17
        );

        addRegular(
                "cual es la negacion de: todos aprobaron?",
                new String[]{
                    "existe alguien que no aprobo",
                    "nadie aprobo",
                    "todos reprobaron",
                    "existe alguien que aprobo"
                },
                "existe alguien que no aprobo", "cuantificadores", "media", 15
        );

        addRegular(
                "que expresa ∀x∃y conoce(x, y)?",
                new String[]{
                    "cada persona conoce al menos a una persona",
                    "existe una persona conocida por todos",
                    "todos conocen a todos",
                    "nadie conoce a otra persona"
                },
                "cada persona conoce al menos a una persona", "cuantificadores", "media", 17
        );
    }

    // crea 12 preguntas de teoria de conjuntos
    private void addSetPuzzles() {
        addRegular(
                "A = {1, 2, 3} y B = {3, 4, 5}. calcula A ∪ B.",
                new String[]{"{1, 2, 3, 4, 5}", "{3}", "{1, 2}", "{4, 5}"},
                "{1, 2, 3, 4, 5}", "teoria de conjuntos", "facil", 13
        );

        addRegular(
                "A = {1, 2, 3} y B = {3, 4, 5}. calcula A ∩ B.",
                new String[]{"{3}", "{1, 2, 3, 4, 5}", "{1, 2}", "{4, 5}"},
                "{3}", "teoria de conjuntos", "facil", 13
        );

        addRegular(
                "A = {2, 4, 6, 8} y B = {4, 8}. calcula A - B.",
                new String[]{"{2, 6}", "{4, 8}", "{2, 4, 6, 8}", "{}"},
                "{2, 6}", "teoria de conjuntos", "facil", 13
        );

        addRegular(
                "U = {1, 2, 3, 4, 5, 6} y A = {2, 4, 6}. calcula A'.",
                new String[]{"{1, 3, 5}", "{2, 4, 6}", "{1, 2, 3, 4, 5, 6}", "{}"},
                "{1, 3, 5}", "teoria de conjuntos", "facil", 14
        );

        addRegular(
                "A = {1, 2, 3, 4}, B = {3, 4, 5} y C = {4, 5, 6}. calcula (A ∩ B) ∪ C.",
                new String[]{"{3, 4, 5, 6}", "{4}", "{1, 2, 3, 4, 5, 6}", "{3, 5}"},
                "{3, 4, 5, 6}", "teoria de conjuntos", "media", 17
        );

        addRegular(
                "A = {a, b} y B = {a, b, c}. cual relacion es correcta?",
                new String[]{"A ⊆ B", "B ⊆ A", "A = B", "A ∩ B = {}"},
                "A ⊆ B", "teoria de conjuntos", "facil", 13
        );

        addRegular(
                "si |A| = 4, cuantos elementos tiene el conjunto potencia P(A)?",
                new String[]{"16", "8", "4", "24"},
                "16", "teoria de conjuntos", "media", 15
        );

        addRegular(
                "si |A| = 8, |B| = 7 y |A ∩ B| = 3, cuanto vale |A ∪ B|?",
                new String[]{"12", "18", "15", "10"},
                "12", "teoria de conjuntos", "media", 16
        );

        addRegular(
                "A = {1, 2, 3} y B = {3, 4}. calcula la diferencia simetrica A △ B.",
                new String[]{"{1, 2, 4}", "{3}", "{1, 2, 3, 4}", "{1, 2}"},
                "{1, 2, 4}", "teoria de conjuntos", "media", 16
        );

        addRegular(
                "que identidad es correcta?",
                new String[]{
                    "(A ∪ B)' = A' ∩ B'",
                    "(A ∪ B)' = A' ∪ B'",
                    "(A ∩ B)' = A' ∩ B'",
                    "A - B = B - A"
                },
                "(A ∪ B)' = A' ∩ B'", "teoria de conjuntos", "media", 16
        );

        addRegular(
                "si |A| = 3 y |B| = 4, cuantos pares ordenados tiene A × B?",
                new String[]{"12", "7", "64", "1"},
                "12", "teoria de conjuntos", "media", 15
        );

        addRegular(
                "A = {1, 2, 3, 4, 5}, B = {2, 4, 6} y C = {1, 5, 6}. calcula (A - B) ∩ C.",
                new String[]{"{1, 5}", "{2, 4}", "{1, 3, 5}", "{6}"},
                "{1, 5}", "teoria de conjuntos", "media", 17
        );
    }

    // crea 14 preguntas especiales rompe racha
    private void addBreakerPuzzles() {
        addBreaker(
                """
                sea p = verdadero, q = falso, r = verdadero y s = falso.

                evalua:
                ((P → Q) ∨ (R ∧ ¬S)) ∧ (Q ↔ S)
                """,
                booleanOptions(true), "verdadero", "logica anidada", "rompe racha", 25
        );

        addBreaker(
                """
                simplifica la expresion:

                ¬((P ∨ Q) → (R ∧ S))
                """,
                new String[]{
                    "(P ∨ Q) ∧ (¬R ∨ ¬S)",
                    "(¬P ∧ ¬Q) ∨ (R ∧ S)",
                    "(P ∨ Q) → (¬R ∧ ¬S)",
                    "(¬P ∨ ¬Q) ∧ (R ∨ S)"
                },
                "(P ∨ Q) ∧ (¬R ∨ ¬S)", "equivalencias", "rompe racha", 28
        );

        addBreaker(
                """
                niega correctamente:

                ∀x(P(x) → ∃y Q(x, y))
                """,
                new String[]{
                    "∃x(P(x) ∧ ∀y ¬Q(x, y))",
                    "∀x(P(x) ∧ ∃y ¬Q(x, y))",
                    "∃x(¬P(x) ∧ ∀y Q(x, y))",
                    "∀x(P(x) → ∀y ¬Q(x, y))"
                },
                "∃x(P(x) ∧ ∀y ¬Q(x, y))", "cuantificadores", "rompe racha", 30
        );

        addBreaker(
                """
                premisas:
                P → Q
                Q → R
                ¬R

                cual conclusion necesariamente sigue?
                """,
                new String[]{"¬P", "P", "Q", "R"},
                "¬P", "argumentos", "rompe racha", 25
        );

        addBreaker(
                """
                un caballero siempre dice la verdad.
                un mentiroso siempre miente.

                a dice: b es mentiroso.
                b dice: a y yo somos de tipos distintos.

                quienes son?
                """,
                new String[]{
                    "a es mentiroso y b es caballero",
                    "a es caballero y b es mentiroso",
                    "ambos son caballeros",
                    "ambos son mentirosos"
                },
                "a es mentiroso y b es caballero", "caballeros y mentirosos", "rompe racha", 30
        );

        addBreaker(
                "cual formula expresa que exactamente una de P y Q es verdadera?",
                new String[]{
                    "(P ∨ Q) ∧ ¬(P ∧ Q)",
                    "P ∧ Q",
                    "P ↔ Q",
                    "¬P ∧ ¬Q"
                },
                "(P ∨ Q) ∧ ¬(P ∧ Q)", "logica exclusiva", "rompe racha", 24
        );

        addBreaker(
                """
                para cuantas asignaciones de P, Q y R es verdadera la formula?

                (P ∨ Q) ∧ (¬P ∨ R)
                """,
                new String[]{"4", "3", "5", "6"},
                "4", "tabla de verdad", "rompe racha", 30
        );

        addBreaker(
                """
                U = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
                A = {1, 2, 4, 7, 10}
                B = {2, 3, 4, 5, 8}
                C = {1, 4, 5, 6, 10}

                calcula (A ∩ (B ∪ C))'.
                """,
                new String[]{
                    "{3, 5, 6, 7, 8, 9}",
                    "{1, 2, 4, 10}",
                    "{3, 6, 8, 9}",
                    "{5, 6, 7, 8, 9}"
                },
                "{3, 5, 6, 7, 8, 9}", "conjuntos anidados", "rompe racha", 32
        );

        addBreaker(
                """
                U = {1, 2, 3, 4, 5, 6, 7, 8, 9}
                A = {1, 2, 3, 4, 5}
                B = {2, 4, 6, 8}
                C = {1, 4, 7, 9}

                calcula (A ∪ B) ∩ C'.
                """,
                new String[]{
                    "{2, 3, 5, 6, 8}",
                    "{1, 4, 7, 9}",
                    "{2, 4}",
                    "{3, 5, 6, 8, 9}"
                },
                "{2, 3, 5, 6, 8}", "conjuntos anidados", "rompe racha", 30
        );

        addBreaker(
                "cual expresion es equivalente a (A - B) ∪ (B - A)?",
                new String[]{
                    "(A ∪ B) - (A ∩ B)",
                    "A ∩ B",
                    "(A ∩ B) - (A ∪ B)",
                    "A - (A ∪ B)"
                },
                "(A ∪ B) - (A ∩ B)", "diferencia simetrica", "rompe racha", 26
        );

        addBreaker(
                """
                dominio D = {1, 2, 3, 4}.

                cual es el valor de:
                ∀x∃y(x + y = 5)
                """,
                booleanOptions(true), "verdadero", "cuantificadores", "rompe racha", 26
        );

        addBreaker(
                """
                dominio: numeros enteros positivos.

                cual es el valor de:
                ∃x∀y(x + y = y)
                """,
                booleanOptions(false), "falso", "cuantificadores", "rompe racha", 27
        );

        addBreaker(
                """
                si el servidor falla, suena la alarma.
                si suena la alarma, el operador despierta.
                si el operador despierta, el sistema reinicia.
                el sistema no reinicio.

                que debe concluirse?
                """,
                new String[]{
                    "el servidor no fallo",
                    "el servidor fallo",
                    "la alarma sono",
                    "el operador desperto"
                },
                "el servidor no fallo", "argumento encadenado", "rompe racha", 30
        );

        addBreaker(
                """
                tres personas a, b y c son caballeros o mentirosos.
                a dice: b y c son del mismo tipo.
                b dice: a es mentiroso.
                c dice: b es caballero.

                cual asignacion es consistente?
                """,
                new String[]{
                    "a caballero, b mentiroso, c mentiroso",
                    "a mentiroso, b caballero, c caballero",
                    "a caballero, b caballero, c mentiroso",
                    "los tres son mentirosos"
                },
                "a caballero, b mentiroso, c mentiroso", "caballeros y mentirosos", "rompe racha", 35
        );
    }

    // agrega una pregunta normal y mezcla sus respuestas
    private void addRegular(
            String question,
            String[] options,
            String correctAnswer,
            String category,
            String difficulty,
            int timeLimit
    ) {
        regularPool.add(createShuffledPuzzle(
                question, options, correctAnswer, category, difficulty, timeLimit, false
        ));
    }

    // agrega una pregunta rompe racha y mezcla sus respuestas
    private void addBreaker(
            String question,
            String[] options,
            String correctAnswer,
            String category,
            String difficulty,
            int timeLimit
    ) {
        breakerPool.add(createShuffledPuzzle(
                question, options, correctAnswer, category, difficulty, timeLimit, true
        ));
    }

    // mezcla las opciones y encuentra la nueva posicion correcta
    private Puzzle createShuffledPuzzle(
            String question,
            String[] options,
            String correctAnswer,
            String category,
            String difficulty,
            int timeLimit,
            boolean breaker
    ) {
        List<String> mixedOptions = new ArrayList<>(List.of(options));
        Collections.shuffle(mixedOptions, random);

        String[] shuffledOptions = mixedOptions.toArray(String[]::new);
        int correctIndex = mixedOptions.indexOf(correctAnswer);

        if (correctIndex < 0) {
            throw new IllegalArgumentException("la respuesta correcta no esta entre las opciones");
        }

        return new Puzzle(
                question,
                shuffledOptions,
                correctIndex,
                category,
                difficulty,
                timeLimit,
                breaker
        );
    }

    // crea opciones estandar para una pregunta verdadera o falsa
    private String[] booleanOptions(boolean result) {
        Set<String> options = new LinkedHashSet<>();
        options.add(result ? "verdadero" : "falso");
        options.add(result ? "falso" : "verdadero");
        options.add("no se puede determinar");
        options.add("depende del dominio");
        return options.toArray(String[]::new);
    }

    private String truthWord(boolean value) {
        return value ? "verdadero" : "falso";
    }

    // representa una expresion que usa tres valores de verdad
    @FunctionalInterface
    private interface TruthExpression {
        boolean evaluate(boolean p, boolean q, boolean r);
    }

    // guarda el texto y la regla de una plantilla logica
    private static class TruthTemplate {
        private final String text;
        private final TruthExpression expression;

        private TruthTemplate(String text, TruthExpression expression) {
            this.text = text;
            this.expression = expression;
        }
    }
}
