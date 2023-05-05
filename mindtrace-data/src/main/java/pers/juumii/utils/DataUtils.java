package pers.juumii.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataUtils {

    public static  <T> T getIf(Collection<T> collection, Function<T, Boolean> predicate){
        T res = null;
        for(T e: collection)
            if(predicate.apply(e))
                res = e;
        return res;
    }
    public static  <T> List<T> getAllIf(Collection<T> collection, Function<T, Boolean> predicate){
        List<T> res = new ArrayList<>();
        if(collection == null)
            return res;
        for(T e: collection)
            if(predicate.apply(e))
                res.add(e);
        return res;
    }


    public static <T, R> List<R> destructureAll(Collection<T> collection, Function<T, R> transformation){
        if(collection == null)
            collection = new ArrayList<>();
        List<R> res = new ArrayList<>();
        for(T item: collection)
            res.add(transformation.apply(item));
        return res;
    }

    //对符合条件的数据进行某些操作，这些操作无返回值
    public static <T> void forAllIf(Collection<T> collection, Function<T, Boolean> predicate, Consumer<T> operation){
        for(T e: collection)
            if(predicate.apply(e))
                operation.accept(e);
    }

    public static <T> boolean ifAll(Collection<T> collection, Function<T, Boolean> predicate){
        for (T e: collection)
            if(!predicate.apply(e))
                return false;
        return true;
    }

    public static <T> boolean ifAny(Collection<T> collection, Function<T, Boolean> predicate){
        for (T e: collection)
            if(predicate.apply(e))
                return true;
        return false;
    }

    public static <T> boolean satisfy(Collection<T> collection, Function<Collection<T>, Boolean> predicate){
        if(collection.isEmpty())
            return false;
        return predicate.apply(collection);
    }

    public static <T> Stack<T> stackOf(Collection<T> collection){
        Stack<T> stack = new Stack<>();
        collection.forEach(stack::push);
        return stack;
    }

    public static <T> Stack<T> reverseStackOf(List<T> list){
        return stackOf(reverse(list));
    }


    public static <T> T getLast(List<T> list){
        return list.isEmpty() ? null: list.get(list.size() - 1);
    }

    public static <T> List<T> join(Collection<List<T>> groups) {
        List<T> res = new ArrayList<>();
        groups.forEach(res::addAll);
        return res;
    }

    public static <T> List<T> join(List<T> first, List<T> second) {
        List<T> res = new ArrayList<>();
        res.addAll(first);
        res.addAll(second);
        return res;
    }

    public static <T> List<T> join(T first, List<T> second) {
        List<T> res = new ArrayList<>();
        res.add(first);
        res.addAll(second);
        return res;
    }

    public static <T> List<T> join(List<T> first, T second) {
        List<T> res = new ArrayList<>(first);
        res.add(second);
        return res;
    }

    public static <T> List<T> reverse(List<T> list) {
        Stack<T> ori = stackOf(list);
        Stack<T> reverse = new Stack<>();
        while (!ori.isEmpty())
            reverse.push(ori.pop());
        return reverse;
    }

    public static <T> List<T> intersection(List<T> first, List<T> second) {
        return getAllIf(first, second::contains);
    }

    public static <T> List<T> returnSorted(Collection<T> collection, Comparator<T> comparator){
        List<T> res = new ArrayList<>(List.copyOf(collection));
        res.sort(comparator);
        return res;
    }

    public static <T> List<T> returnSorted(Collection<T> collection, Comparator<T> comparator, Boolean reverse){
        return reverse ? reverse(returnSorted(collection, comparator)) : returnSorted(collection, comparator);
    }

    @SafeVarargs
    public static <T> void layeredSort(List<T> list, Comparator<T>... comparators){
        //将这些comparators按序组合为一个新的comparator：如果第一个比较相等则考虑第二个，如果第二个比较相等则考虑第三个，以此类推
        list.sort((o1, o2) -> {
            for (Comparator<T> comparator: comparators)
                if(comparator.compare(o1, o2) != 0)
                    return comparator.compare(o1, o2);
            return 0;
        });
    }

    public static <T> void weightedSort(List<T> list, Map<Function<T,Double>, Double> weightMap){
        list.sort(Comparator.comparing(element -> {
            double rate = 0;
            for(Function<T, Double> evaluator: weightMap.keySet())
                rate += evaluator.apply(element) * weightMap.get(evaluator);
            return rate;
        }));
    }

    public static <K, T> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> assign) {
        Map<K, List<T>> res = new HashMap<>();
        for(T element: collection){
            K key = assign.apply(element);
            if(res.containsKey(key))
                res.get(key).add(element);
            else res.put(key, new ArrayList<>(List.of(element)));
        }
        return res;
    }

    public static <T> List<T> deNull(List<T> list) {
        return getAllIf(list, Objects::nonNull);
    }

    public static <T> List<T> deduplicate(List<T> ori) {
        ArrayList<T> res = new ArrayList<>();
        for(T e: ori)
            if(!res.contains(e))
                res.add(e);
        return res;
    }

    public static <T> boolean arrayContains(T[] arr, T target) {
        if(arr == null)
            return false;
        for(T e: arr)
            if(e.equals(target))
                return true;
        return false;
    }

    public static Number sum(List<Number> nums){
        Number res = 0;
        for(Number num: nums)
            res = res.doubleValue() + num.doubleValue();
        return res;
    }

    public static <T> T getIfLast(List<T> list, Function<T, Boolean> predicate) {
        T res = null;
        for(T e: DataUtils.reverse(list))
            if(predicate.apply(e))
                res = e;
        return res;
    }



    public static <T> List<T> tail(List<T> list, int count) {
        List<T> res = new ArrayList<>();
        Stack<T> stack = stackOf(list);
        while (!stack.isEmpty() && count-- > 0)
            res.add(stack.pop());
        return res;
    }


    public static <T> List<T> selectByIndexes(List<T> list, List<Integer> indexes) {
        List<T> res = new ArrayList<>();
        for(int i = 0; i < list.size(); i ++)
            if(indexes.contains(i))
                res.add(list.get(i));
        return res;
    }

    public static <T> List<T> arrayToList(T[] arr) {
        if(arr == null) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(arr));
    }

    public static <T> List<T> subList(List<T> list, int start, int end) {
        if(list.isEmpty()) return list;
        return list.subList(Math.min(start, list.size()), Math.min(end, list.size()));
    }
}