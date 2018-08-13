/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016

 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment

import edu.illinois.cs.cogcomp.core.datastructures.IntPair
import edu.illinois.cs.cogcomp.core.datastructures.Pair
import java.io.File
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * @author Xiaotian Le
 */
object CrossValidationUtilKt {

    @JvmStatic
    @Throws(IOException::class)
    fun splitColumnFormat(entireFile: String, numFolds: Int) {
        val sentences = ArrayList<List<String>>()

        File(entireFile).useLines { lines ->
            val sentence = ArrayList<String>()
            lines.forEach { line ->
                if (line.isEmpty()) {
                    sentences.add(ArrayList(sentence))
                    sentence.clear()
                } else {
                    sentence.add(line)
                }
            }
        }

        val data = makeCrossValidationData(sentences, numFolds)

        val writeSentences = { newSentences: List<List<String>>, partFile: String ->
            File(partFile).writeText(newSentences
                    .map { sentence -> sentence.joinToString("\n") }
                    .joinToString("\n\n", postfix = "\n\n"))
        }

        for (setId in 0..numFolds - 1) {
            writeSentences(data[setId].trainingData, "${entireFile}_fold$setId.train")
            writeSentences(data[setId].testingData, "${entireFile}_fold$setId.test")
        }
    }

    @JvmStatic
    fun makeFoldIndices(count: Int, numFolds: Int): List<IntPair> {
        val splitPoints = IntStream.rangeClosed(0, numFolds).boxed()
                .map { fold -> count * fold / numFolds }
                .collect(Collectors.toList())
        return IntStream.range(0, numFolds).boxed()
                .map { fold -> IntPair(splitPoints[fold], splitPoints[fold + 1]) }
                .collect(Collectors.toList())
    }

    @JvmStatic
    fun <T> makeFolds(elements: List<T>, numFolds: Int): List<List<T>> {
        return makeFoldIndices(elements.size, numFolds)
                .map({ indices -> elements.subList(indices.first, indices.second) })
    }

    @JvmStatic
    fun <T> makeCrossValidationData(data: List<T>, numFolds: Int): List<DataPair<T>> {
        val dataFolds = makeFolds(data, numFolds)

        val results = ArrayList<DataPair<T>>()
        for (dataPairId in 0..numFolds - 1) {
            val resultData = DataPair<T>()
            for (fold in 0..numFolds - 1) {
                if (dataPairId != fold) {
                    resultData.trainingData.addAll(dataFolds[fold])
                } else {
                    resultData.testingData.addAll(dataFolds[fold])
                }
            }
            results.add(resultData)
        }
        return results
    }

    class DataPair<T> : Pair<MutableList<T>, MutableList<T>>(ArrayList<T>(), ArrayList<T>()) {
        val trainingData: MutableList<T> = first
        val testingData: MutableList<T> = second
    }
}
