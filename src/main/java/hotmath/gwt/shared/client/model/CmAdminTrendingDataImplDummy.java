package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.util.CmException;

/**
 * Provide a dummy CmAdminTrendingData implementation for testing.
 * 
 * @author casey
 * 
 */
public class CmAdminTrendingDataImplDummy implements CmAdminTrendingDataI {

    CmList<TrendingData> trendingData = new CmArrayList<TrendingData>();
    AccountInfoModel adminModel;

    public CmAdminTrendingDataImplDummy() {
        int maxLessons = 5;
        for (int i = 0; i < maxLessons; i++) {
            trendingData.add(getRandom());
        }
    }

    @Override
    public CmList<TrendingData> getTrendingData() {
        return this.trendingData;
    }

    private TrendingData getRandom() {
        try {
            for (int tries=5; tries > 0; --tries) {
                String ln = __sampleData[getRandNumber(__sampleData.length - 1)];
                for (TrendingData td : trendingData) {
                    if (td.getLessonName().equals(ln))
                        continue;
                }
                return new TrendingData(ln, getRandNumber(1000));
            }
            throw new CmException("Could not create sample TrendingData");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getRandNumber(int max) {
        double dbl = (Math.random() * max);
        return (int) dbl;
    }

    static final String __sampleData[] = { "lesson_name", "Inverse Trigonometric Functions", "Law of Cosines",
            "Trigonometric Ratios", "Area", "Angle of Elevation", "Sum of Infinite Geometric Series", "Word Problems",
            "nth term of an Arithmetic Sequence", "Distance Formula", "Standard Form of a Circle",
            "Completing the Squares", "Conic Sections and Standard Froms of Equations", "Property of Logarithms",
            "Solving Quadratic Equations", "Inverse Functions", "Solving Linear Equations",
            "Logarithmic to Exponential Form", "Exponential Decay", "Solving Quadratic Equations Using Factoring",
            "Operations with Complex Numbers", "Properties of Exponents", "Power of a Power Property", "Variables",
            "Properties of Logarithms", "The Coordinate Plane", "Exponents", "Factoring", "Reciprocals",
            "Negative Exponents", "Determinants", "Simplest Form of a Fraction", "Dividing by a Fraction",
            "Multiplying a Fraction by a Fraction", "Multiplying a Fraction by an Integer",
            "Adding and Subtracting Fractions", "Least Common Multiples", "Least Common Denominators", "LCM",
            "Adding and Subtracting Decimals", "Prime and Composite Numbers", "Prime Factorization",
            "Transformations of Graphs", "LCD", "Solving Quadratic equations by Square Roots", "Number Line",
            "Multiplying and Dividing Negatives", "Adding and Subtracting Negatives", "Absolute Value",
            "Order of Operations", "Parallel Lines", "Alternate Interior Angles Theorem",
            "Corresponding Angles Postulate", "Congruent Angles", "GCF", "Root", "Quadratic Formula", "Rectangle",
            "Reducing Fractions", "Divisibility Tests", "Proportions", "Square", "Parallelogram", "Quadrilaterals",
            "Kite", "Simplifying Rational Expressions" };

    @Override
    public CmList<ProgramData> getProgramData() {
        // TODO Auto-generated method stub
        return null;
    }
}
