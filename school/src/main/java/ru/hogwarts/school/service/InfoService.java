package ru.hogwarts.school.service;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Вспомогательный сервис - для не относящихся к основной логике методов, вызываемых в контроллере
 */

@Service
public class InfoService {

    private static final Logger logger = LoggerFactory.getLogger(InfoService.class);

    /**
     * Представлено 3 метода подсчета суммы значений в заданном диапазоне, для получения результата за наименьшее время по сравнению с предложенным алгоритмом (исходный код) <br/>
     * По итогу произведенных вычислений, наиболее быстрый подсчет производится с помощью метода getSumArithmeticProgression() для него и создан эндпоинт в InfoController <br/>
     * Параллельный стрим лучше не использовать, т.к. время подсчета при этом увеличивается втрое по сравнению с последовательным выполнением
     */

    @Operation(summary = "Вычисление целочисленного значения по заданной формуле (исходный код)")
    public int sumSource() {
        long startTime = System.currentTimeMillis();
        int sum = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, (a, b) -> a + b);
        long endTime = System.currentTimeMillis();
        logger.info("Calculation of the sum by the method sumSource took {} ms", (endTime - startTime));
        System.out.println("Результат вычисления методом sumSource = " + sum + " Time = " + (endTime - startTime));
        return sum;
    }

    @Operation(summary = "Вычисление с использованием переменной типа Long вместо int из-за переполнения допустимого значения")
    public Long getSum() {
        long startTime = System.currentTimeMillis();
        Long sum = Stream.iterate(1L, a -> a + 1)
                .limit(1_000_000)
                .reduce(0L, (a, b) -> a + b);
        long endTime = System.currentTimeMillis();
        logger.info("Calculation of the sum by the method getSum took {} ms", (endTime - startTime));
        System.out.println("Результат вычисления методом getSum = " + sum + " Time = " + (endTime - startTime));
        return sum;
    }

    public Long getSumParallelStream() {
        long startTime = System.currentTimeMillis();
        Long sum = LongStream.iterate(1L, a -> a + 1)
                .limit(1_000_000L)
                .parallel()
                .reduce(0L, Long::sum);
        long endTime = System.currentTimeMillis();
        logger.info("Parallel stream calculation took {} ms", (endTime - startTime));
        System.out.println("Результат вычисления методом getSumParallelStream = " + sum + " Time = " + (endTime - startTime));
        return sum;
    }

    public long getSumArithmeticProgression() {
        long startTime = System.currentTimeMillis();
        int n = 1_000_000;
        long sum = (long) n * (n + 1) / 2;
        long endTime = System.currentTimeMillis();
        logger.info("Arithmetic progression calculation took {} ms", (endTime - startTime));
        System.out.println("Результат вычисления методом getSumArithmeticProgression = " + sum + " Time = " + (endTime - startTime));
        return sum;
    }

}
