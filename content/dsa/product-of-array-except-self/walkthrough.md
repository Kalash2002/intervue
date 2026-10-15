# How a strong round goes: Product of Array Except Self

## Approach

The naive way multiplies the other numbers again for every position, which is quadratic. The repeated work is that neighbouring positions share almost all of their product. For position `i`, the answer is the product of everything to the left times the product of everything to the right. The left products grow as I move right, one multiplication each step, and the right products do the same from the other end. So I sweep left to right, writing into each slot the product of everything before it. Then I sweep right to left, keeping a running product of everything after the slot, and multiply it into the slot. Dividing the total by `nums[i]` looks shorter but breaks on zeros: with one zero, the slot of the zero needs the product of the others, and every other slot is zero. With two zeros everything is zero. The two sweeps handle all of this without special cases, because a zero simply makes every product that passes it zero.

```java
static int[] exceptSelf(int[] nums) {
    int n = nums.length;
    int[] out = new int[n];
    int left = 1; // product of everything before the current slot
    for (int i = 0; i < n; i++) {
        out[i] = left;
        left *= nums[i];
    }
    int right = 1; // product of everything after the current slot
    for (int i = n - 1; i >= 0; i--) {
        out[i] *= right;
        right *= nums[i];
    }
    return out;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Multiply all the others for each position | O(n squared) | O(1) besides the answer |
| Total product divided by each number | O(n), but wrong with zeros | O(1) |
| Left sweep then right sweep | O(n) | O(1) besides the answer |

## Where it shows up

Prefix and suffix products (or sums) answer "everything except this one" questions without recomputing: leave-one-out statistics in analytics, "total minus this item's share" in billing, and the left and right running values used to compute things like compound growth up to and after a given day.

## Follow-up answers

**Can you use only O(1) extra space besides the answer array?** Yes, that is the version above: the answer array holds the left products, and the right product is a single variable.

**Why is dividing the total product by each number a poor idea here?** A zero makes the total zero and division by zero is not defined, so the code needs separate cases for no zero, one zero and several zeros. The two sweeps need none of that, and they also avoid exact-division surprises with other number types.

**How would you answer many queries after changing single values in the array?** Keep the products in a segment tree or a Fenwick-style structure. A point update then costs O(log n), and the product of everything except one position is the product of the range before it times the range after it.
