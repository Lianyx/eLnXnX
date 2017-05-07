package myxiaoxiaole;

import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//不确定用不用这个
public class GridOperator {

    private static final List<Integer> traversalX = IntStream.range(0, GridPanel.CELL_X).boxed().collect(Collectors.toList());
    private static final List<Integer> traversalY = IntStream.range(0, GridPanel.CELL_Y).boxed().collect(Collectors.toList());

    public static /*int*/void traverseGrid(IntBinaryOperator func) {
        //这个应该是为了计算可以移动的个数
//        AtomicInteger at = new AtomicInteger();
        traversalX.forEach(x -> {
            traversalY.forEach(y -> {
//                at.addAndGet(func.applyAsInt(x, y));
                func.applyAsInt(x, y);
            });
        });
//        return at.get();
    }

//    public static void sortGrid(Direction direction){
//        // TO-DO: Step 26. Sort TraversalX, traversalY, so for Right or Down directions
//        // they are taken in reverse order
//        Collections.sort(traversalX, direction.equals(Direction.RIGHT) ? Collections.reverseOrder() : Integer::isSameColor);
//        Collections.sort(traversalY, direction.equals(Direction.DOWN)? Collections.reverseOrder() : Integer::isSameColor);
//        // -->
//    }
}