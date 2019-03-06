package top.yangxf.interest.algorithm.theory;

import java.util.concurrent.ThreadLocalRandom;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * 模拟三门问题。
 *
 * <pre>
 * 现在有三扇门，只有一扇门有汽车，其余两扇门的都是山羊。
 * 汽车事前是等可能地被放置于三扇门的其中一扇后面。
 * 参赛者在三扇门中挑选一扇。他在挑选前并不知道任意一扇门后面是什麽。
 * 主持人知道每扇门后面有什么。
 * 如果参赛者挑了一扇有山羊的门，主持人必须挑另一扇有山羊的门。
 * 如果参赛者挑了一扇有汽车的门，主持人等可能地在另外两扇有山羊的门中挑一扇门。
 * 参赛者会被问是否保持他的原来选择，还是转而选择剩下的那一扇门。
 * </pre>
 *
 * <pre>
 * 1. 如果不改变自己的选择，那么主持人是否排除选项和选择无关了，选到汽车的概率就是1/3
 *
 * 2. 如果改变自己的选择，那么主持人排除一个错误答案之后，
 *    只有之前选择了羊，改变选择之后才能选到汽车，而之前选到羊的概率是2/3
 *
 * 所以，改变选择能提高选到汽车的概率。
 * </pre>
 *
 * @author yangxf
 */
public class MontyHallSimulator implements Simulator {

    private static final int DEFAULT_N_DOORS = 3;
    private int sampleSize;

    /**
     * @param sampleSize 模拟次数
     */
    public MontyHallSimulator(int sampleSize) {
        if (sampleSize < 0) {
            throw new IllegalStateException("sampleSize must be >= 0");
        }
        this.sampleSize = sampleSize;
    }

    @Override
    public void start() {
        int swapTotal = 0, noSwapTotal = 0,
                swapHits = 0, noSwapHits = 0;
        for (int i = 0; i < sampleSize; i++) {
            int[] doors = newDoors(DEFAULT_N_DOORS);
            Compere compere = new Compere(doors);
            Guest guest = new Guest();
            // 选择一道门
            int guestSelect = guest.select(doors.length);
            // 主持人排除一个错误答案
            int compereExcluded = compere.excluded(guestSelect);
            // 再次选择
            guestSelect = guest.select(doors.length, compereExcluded);
            // 是否改变了自己的选择
            boolean isSwap = guest.isSwap();
            boolean hit = doors[guestSelect] == 1;
            if (isSwap) {
                swapTotal++;
                if (hit) {
                    swapHits++;
                }
            } else {
                noSwapTotal++;
                if (hit) {
                    noSwapHits++;
                }
            }
        }

        double swapHitRate = swapHits / (double) swapTotal,
                noSwapHitRate = noSwapHits / (double) noSwapTotal;
        System.out.println("swap hit rate    : " + swapHitRate);
        System.out.println(swapHits + " " + swapTotal);
        System.out.println("no swap hit rate : " + noSwapHitRate);
        System.out.println(noSwapHits + " " + noSwapTotal);
    }

    /**
     * 随机放置一辆汽车
     */
    private static int[] newDoors(int nDoors) {
        if (nDoors < 1) {
            throw new IllegalStateException("nDoors must be > 0");
        }
        int[] doors = new int[nDoors];
        doors[nextInt(nDoors)] = 1;
        return doors;
    }

    private static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    static class Compere {
        int[] doors;

        /**
         * @param doors 主持人事先知道门后都有什么
         */
        Compere(int[] doors) {
            checkNotNull(doors);
            this.doors = doors;
        }

        /**
         * 根据嘉宾的选择，排除一个错误答案（门后是羊的）
         *
         * @param selectOfGuest 嘉宾选择的门的index
         * @return 排除的门index
         */
        int excluded(int selectOfGuest) {
            int[] remainIndices = new int[doors.length - 1];
            for (int i = 0, ri = 0; i < doors.length; i++) {
                if (i != selectOfGuest) {
                    remainIndices[ri++] = i;
                }
            }

            // 如果嘉宾选择了羊，那么只能返回另外一个羊的index
            if (doors[selectOfGuest] == 0) {
                for (int remainIndex : remainIndices) {
                    if (doors[remainIndex] == 0) {
                        return remainIndex;
                    }
                }
            }

            // 否则（嘉宾第一次选到了汽车），随机返回一个羊的index
            return remainIndices[nextInt(remainIndices.length)];
        }

    }

    static class Guest {

        /**
         * 是否改变选择
         */
        boolean swap;

        /**
         * 选择的门index
         */
        int select;

        /**
         * 选择一道门
         *
         * @param nDoors 门数量
         * @return 选择的门的index
         */
        int select(int nDoors) {
            select = nextInt(nDoors);
            return select;
        }

        /**
         * 在主持人排除一道门后，再次选择
         *
         * @param nDoors          门数量
         * @param compereExcluded 主持人排除的门的index
         * @return 选择的门的index
         */
        int select(int nDoors, int compereExcluded) {
            int newSelect = select;
            // 是否改变选择是个随机事件
            if (ThreadLocalRandom.current().nextBoolean()) {
                swap = true;
                // 改变第一次的选择，则返回另外一道门
                for (int i = 0; i < nDoors; i++) {
                    if (i != select && i != compereExcluded) {
                        newSelect = i;
                    }
                }
                select = newSelect;
            }
            return newSelect;
        }

        /**
         * @return true表示在主持人排除一道门之后，改变了自己的选择
         */
        boolean isSwap() {
            return swap;
        }

    }

}