package com.rock.search.engine

/**
 * Model used to represent Search Relevance through Comparison operators for search inputs.
 * Each Comparison type is associated with a score between 100 to 0, 100 being complete match and 0 being no match.
 * The scores are used in rank calculation of matching Files in the Search Response.
 *
 * The comparison types have a hierarchical relationship as follow:
 * - The hierarchy from top to bottom is determined by the degree of strictness. The comparison types at the top are
 *    more strict than the bottom ones. The types at the top have more points than bottom.
 * - It is the responsibility of the the bottom type to exclude the subset of records matched by the higher type.
 *
 *    For eg: Hierarchy defined below is: `Equals` > `StartsWith` > `Contains` > `Default`
 *    The strictness is in decreasing order from top to bottom of the hierarchy. This is because:
 *      - If a search text satisfies `Equals`, it will definitely satisfy `StartsWith` check whereas the opposite
 *        doesn't hold true.
 *      - Similarly, if a search text satisfies `StartsWith`, it will definitely satisfy `Contains` check whereas
 *        the opposite doesn't hold true.
 *
 *    Therefore it is the responsibility of `StartsWith` that it excludes the matches results of `Equals`. Same goes
 *    for all types. This is achieved referring to `matchCriteria` function of higher types.
 */
sealed abstract class ComparisonType(_point: Int) {
  val point = _point
  protected def matchCriteria(inputWord: String, record: InvertedIndexRecord): Boolean
  def getMatchedRecords(inputWord: String, searchContext: SearchContext): List[InvertedIndexRecord]
}

object ComparisonType {
  case object Equals extends ComparisonType(100) {
    override def matchCriteria(inputWord: String, record: InvertedIndexRecord): Boolean = {
      inputWord.equalsIgnoreCase(record.word)
    }
    override def getMatchedRecords(inputWord: String, searchContext: SearchContext): List[InvertedIndexRecord] = {
      searchContext.records.find(matchCriteria(inputWord, _)).toList
    }
  }

  case object StartsWith extends ComparisonType(90) {
    override def matchCriteria(inputWord: String, record: InvertedIndexRecord): Boolean = {
      (inputWord.startsWith(record.word) || record.word.startsWith(inputWord)) && !Equals.matchCriteria(
        inputWord,
        record
      )
    }
    override def getMatchedRecords(inputWord: String, searchContext: SearchContext): List[InvertedIndexRecord] = {
      searchContext.records.filter(matchCriteria(inputWord, _))
    }
  }

  case object Contains extends ComparisonType(80) {
    override def matchCriteria(inputWord: String, record: InvertedIndexRecord): Boolean = {
      (inputWord.contains(record.word) || record.word.contains(inputWord)) && !StartsWith.matchCriteria(
        inputWord,
        record
      )
    }
    override def getMatchedRecords(inputWord: String, searchContext: SearchContext): List[InvertedIndexRecord] = {
      searchContext.records.filter(matchCriteria(inputWord, _))
    }
  }

  case object Default extends ComparisonType(0) {
    override def matchCriteria(inputWord: String, record: InvertedIndexRecord): Boolean = {
      !Contains.matchCriteria(inputWord, record)
    }
    override def getMatchedRecords(inputWord: String, searchContext: SearchContext): List[InvertedIndexRecord] = {
      searchContext.records.filter(matchCriteria(inputWord, _))
    }
  }

  val All = List(Equals, StartsWith, Contains, Default)

}
