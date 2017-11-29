val fr=sc.textFile("friends_actual.txt")
// split person and friend list by tab
val fr1=fr.map(line => line.split("\t"))
// why do this??
val fr2=fr1.filter(line => (line.size==2))
// split the friend list on ","
val fr3=fr2.map(line => (line(0),line(1).split(",")))
// make int pairs
val fr4=fr3.flatMap(a => a._2.flatMap(b => Array((a._1.toInt,b.toInt))))
// join with itself
val afterjoin=fr4.join(fr4)
// use only the pairs in 2nd column, 1st column only contains common element
val fr5=afterjoin.map(a => a._2)
// remove the pairs which are like (1,1)
val fr6=fr5.filter(a => a._1!=a._2)
// remove pairs that are already friends
val fr7=fr6.subtract(fr4)
// 1 is for reducing later to add the number of mutual friends
val fr8=fr7.map(a => (a,1))
// reduce by pairing same keys
val fr9=fr8.reduceByKey(_+_)
// convert to person {as the key}, (suggestion, number of mutual friends)
val fr10=fr9.map({case ((a,b),c) => (a,(b,c))})
// group to get list of suggestions for each person
val fr11=fr10.groupByKey().mapValues(_.toList)
// map by putting person, list of suggestions in descending order of number of mutual friends (tie resolved by ascending order of IDs)
val fr12=fr11.map(a => (a._1,a._2.sortBy(pair => (-pair._2,pair._1)).map(a => a._1)))
// convert list of suggestions to an array
val fr13=fr12.map(a => (a._1,a._2.toArray))
// take only top 10 suggestions which have maximum number of mutual friends
val fr14=fr13.map(a => (a._1,a._2.take(10)))
// checking for IDs without any friends
val nofr=fr1.filter(a => a.size==1).map(a => a(0).toInt)
// converting type for making it compatible with fr14 rdd so that we can combine them
val nofr1=nofr.map({case (i) => (i,Array[Int]())})
// appending IDs with no friends to the result
val fr15=fr14.union(nofr1)
// formatting the result as per the question
val fr16=fr15.map(a => (a._1.toString + "\t" + a._2.mkString(",")))
// saving the output in text files in /result
fr16.saveAsTextFile("result")