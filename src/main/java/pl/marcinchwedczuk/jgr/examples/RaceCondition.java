package pl.marcinchwedczuk.jgr.examples;

import pl.marcinchwedczuk.jgr.Lock;
import pl.marcinchwedczuk.jgr.Primitives;
import pl.marcinchwedczuk.jgr.Thunk;
import pl.marcinchwedczuk.jgr.Trampoline;

import pl.marcinchwedczuk.jgr.Primitives.*;

import static pl.marcinchwedczuk.jgr.Primitives.*;
import static pl.marcinchwedczuk.jgr.Trampoline.delay;

public class RaceCondition {
    public static void main(String[] args) {
        try {
            Trampoline.run(RaceCondition::mainThunk);
        } catch (InterruptedException e) {
            System.err.println("Process was interrupted...");
            System.exit(1);
        }
    }

    private static Thunk mainThunk() {
        var yy = new YinYang();

        return Trampoline.newLock(lock -> {
            lock.__disable();
            return Trampoline.forkMany(
                        () -> printer(yy, lock, 20),
                        () -> moveYangToYin(yy, lock),
                        () -> moveYinToYang(yy, lock));
            }
        );
    }

    private static Thunk printer(YinYang yy, Lock lock, int maxChecks) {
        // @formatter:off
        return  // rand(1, 23, randWaitTime ->
                delay(1000, () ->
                lock.lock(() ->
                readField(yy::getYin, yin ->
                readField(yy::getYang, yang ->
                lock.unlock(() ->
                add(yin, yang, sum ->
                fmt("yin: %d, yang: %d, sum = %d", yin, yang, sum, formatted ->
                println(formatted, () ->
                subtract(maxChecks, 1, newMaxChecks ->
                gt(newMaxChecks, 0, isGt ->
                iff(isGt,
                    t -> printer(yy, lock, newMaxChecks),
                    f -> Trampoline.stopProgram())))))))))));
        // @formatter:on
    }

    private static Thunk moveYinToYang(YinYang yy, Lock lock) {
        // @formatter:off
        return  rand(1, 100, randWaitTime ->
                delay(randWaitTime, () ->
                lock.lock(() ->
                readField(yy::getYin, yin ->
                subtract(yin, 1, newYin ->
                writeField(newYin, yy::setYin, () ->
                readField(yy::getYang, yang ->
                add(yang, 1, newYang ->
                writeField(newYang, yy::setYang, () ->
                lock.unlock(() ->
                    moveYinToYang(yy, lock)))))))))));
        // @formatter:on
    }

    private static Thunk moveYangToYin(YinYang yy, Lock lock) {
        // @formatter:off
        return  rand(1, 100, randWaitTime ->
                delay(randWaitTime, () ->
                lock.lock(() ->
                readField(yy::getYang, yang ->
                subtract(yang, 1, newYang ->
                writeField(newYang, yy::setYang, () ->
                readField(yy::getYin, yin ->
                add(yin, 1, newYin ->
                writeField(newYin, yy::setYin, () ->
                lock.unlock(() ->
                    moveYangToYin(yy, lock)))))))))));
        // @formatter:on
    }

    private static class YinYang {
        private int yin = 500;
        private int yang = 500;

        public int getYin() {
            return yin;
        }

        public void setYin(int newYin) {
            yin = newYin;
        }

        public int getYang() {
            return yang;
        }

        public void setYang(int newYang) {
            yang = newYang;
        }
    }
}
