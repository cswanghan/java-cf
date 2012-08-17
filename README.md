java-cf
=======

This is a prototype implementation of a user-based collaborative filtering training (similarity matrix and top-K computations) and prediction supporting multi-thread computation through training phase. The applied similarity measure is the cosine similarity and the aggregation technique is the adjusted weighted sum method (For more details, please, see http://ids.csom.umn.edu/faculty/gedas/papers/recommender-systems-survey-2005.pdf, the applied similarity is shown in (6.), the aggregation method is presented at (10. c).)