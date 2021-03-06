package com.joe.leetcode.daily;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 803. 打砖块
 *
 * @author ckh
 * @since 2021/1/16
 */
public class BricksFallingWhenHit {

    private int rows;
    private int cols;

    public static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};

    public int[] hitBricks(int[][] grid, int[][] hits) {
        this.rows = grid.length;
        this.cols = grid[0].length;

        // 第 1 步：把 grid 中的砖头全部击碎，通常算法问题不能修改输入数据，这一步非必需，可以认为是一种答题规范
        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (cols >= 0) System.arraycopy(grid[i], 0, copy[i], 0, cols);
        }
        // 根据数组 hits，将输入的表格 grid 里的对应位置全部设置为 0
        for (int[] hit : hits) {
            copy[hit[0]][hit[1]] = 0;
        }

        // 第 2 步：建图，把砖块和砖块的连接关系输入并查集
        // size 表示二维网格的大小，也表示虚拟的「屋顶」在并查集中的编号
        int size = rows * cols;
        UnionFind unionFind = new UnionFind(size + 1);

        // 将下标为 0 的这一行的砖块与「屋顶」相连
        for (int j = 0; j < cols; j++) {
            if (copy[0][j] == 1) {
                unionFind.union(j, size);
            }
        }

        // 其余网格，如果是砖块向上、向左看一下，如果也是砖块，在并查集中进行合并
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (copy[i][j] == 1) {
                    // 如果上方也是砖块
                    if (copy[i - 1][j] == 1) {
                        unionFind.union(getIndex(i - 1, j), getIndex(i, j));
                    }
                    // 如果左边也是砖块
                    if (j > 0 && copy[i][j - 1] == 1) {
                        unionFind.union(getIndex(i, j - 1), getIndex(i, j));
                    }
                }
            }
        }

        // 第 3 步：按照 hits 的逆序，在 copy 中补回砖块，把每一次因为补回砖块而与屋顶相连的砖块的增量记录到 res 数组中
        int hitsLen = hits.length;
        int[] res = new int[hitsLen];

        for (int i = hitsLen - 1; i >= 0; i--) {
            int x = hits[i][0];
            int y = hits[i][1];
            // 注意：这里不能用 copy，语义上表示，如果原来在 grid 中，这一块是空白，这一步不会产生任何砖块掉落
            // 逆向补回的时候，与屋顶相连的砖块数量也肯定不会增加
            if (grid[x][y] == 0) continue;

            // 补回之前与屋顶相连的砖块数
            int origin = unionFind.getSize(size);
            // 注意：如果补回的这个结点在第 1 行，要告诉并查集它与屋顶相连（逻辑同第 2 步）
            if (x == 0) {
                unionFind.union(y, size);
            }
            // 在 4 个方向上看一下，如果相邻的 4 个方向有砖块，合并它们
            for (int[] direction : DIRECTIONS) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if (inArea(newX, newY) && copy[newX][newY] == 1) {
                    unionFind.union(getIndex(x, y), getIndex(newX, newY));
                }
            }
            // 补回之后与屋顶相连的砖块数
            int current = unionFind.getSize(size);
            // 减去的 1 是逆向补回的砖块（正向移除的砖块）
            // 与 0 比较大小，是因为存在一种情况，添加当前砖块，不会使得与屋顶连接的砖块数更多
            res[i] = Math.max(0, current - origin - 1);

            // 真正补上这个砖块
            copy[x][y] = 1;

        }

        return res;
    }

    /**
     * 输入坐标在二维网格中是否越界
     */
    private boolean inArea(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    /**
     * 二维坐标转换为一维坐标
     * <p>
     * ! 绝了
     * <p>
     * x * cols + y  的范围刚好是 0 ~ (cols * rows - 1), 最后一位刚好表示根
     * 比如 rows = 2; columns = 4;  size = cols * rows = 8; 数组大小为 size+1 = 9
     * 这里转换坐标后的范围刚好是 parent[0~7], 最后一位parent[8] 刚好表示根
     */
    private int getIndex(int x, int y) {
        return x * cols + y;
    }

    private class UnionFind {
        /**
         * 当前结点的父亲结点
         */
        private final int[] parent;
        /**
         * 以当前结点为根结点的子树的结点总数
         */
        private final int[] size;

        public UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        /**
         * 路径压缩，只要求每个不相交集合的「根结点」的子树包含的结点总数数值正确即可，
         * 因此在路径压缩的过程中不用维护数组 size
         */
        public int find(int x) {
            if (x != parent[x]) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return;

            parent[rootX] = rootY;
            // 在合并的时候维护数组 size
            size[rootY] += size[rootX];
        }

        /**
         * @return x 在并查集的根结点的子树包含的结点总数
         */
        public int getSize(int x) {
            int root = find(x);
            return size[root];
        }
    }


    // 标记与顶部相连
    private static final int TOP = 2;
    // 标记有砖块
    private static final int BRICK = 1;
    // 标记无砖块
    private static final int EMPTY = 0;
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public int[] hitBricksV2(int[][] grid, int[][] hits) {
        int[] res = new int[hits.length];
        // 移除所有hits位置的砖块
        for (int[] hit : hits) {
            grid[hit[0]][hit[1]]--;
        }

        // 把所有与top相连的标记为2
        for (int i = 0; i < grid[0].length; i++) {
            dfs(0, i, grid);
        }

        // Add back the hited Bricks
        for (int i = hits.length - 1; i >= 0; i--) {
            int x = hits[i][0], y = hits[i][1];
            grid[x][y]++;
            // 加回去之后的情况为0或1，为1说明原来这里确实有砖块
            if (grid[x][y] == BRICK && isConnected(x, y, grid)) {
                // 当前位置有砖块，而且与顶部相连，做dfs
                res[i] = dfs(x, y, grid) - 1;
            }
        }

        return res;
    }

    private int dfs(int i, int j, int[][] grid) {
        // grid[i][j] == BRICK 代表有砖块
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != BRICK) {
            return 0;
        }
        grid[i][j] = 2;
        return dfs(i + 1, j, grid)
                + dfs(i - 1, j, grid)
                + dfs(i, j + 1, grid)
                + dfs(i, j - 1, grid) + 1;
    }

    // isConnected用来判断当前坐标是否和顶部相连
    private boolean isConnected(int i, int j, int[][] grid) {
        // 在第0行必然相连
        if (i == 0) {
            return true;
        }
        for (int[] d : DIRS) {
            int x = i + d[0], y = j + d[1];
            // 如果周围的四个点有与顶部相连的，那这个点也是与顶部相连的
            if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && grid[x][y] == TOP) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void test() {
        int[][] grids = {
                {1, 0, 0, 0},
                {1, 1, 0, 0}
        };
        int[][] hits = {
                {1, 1},
                {1, 0}
        };

        int[] res = hitBricksV2(grids, hits);
        System.out.println(Arrays.toString(res));
    }

}
