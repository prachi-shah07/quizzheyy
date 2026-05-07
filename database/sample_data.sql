USE ai_quiz_system;

INSERT INTO users (id, full_name, email, password_hash, created_at) VALUES
(1, 'Admin User', 'admin@quizapp.com', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', NOW()),
(2, 'Student Demo', 'student@quizapp.com', 'b2a1f4fd0a460606b34c8913e2981dac8d2e283d778aba586c416ee2629bfa54', NOW());

INSERT INTO questions (id, topic, difficulty_level, question_text, option_a, option_b, option_c, option_d, correct_option, explanation) VALUES
(1, 'Java Basics', 1, 'Which keyword is used to inherit a class in Java?', 'this', 'extends', 'implements', 'super', 'B', 'The extends keyword is used for class inheritance in Java.'),
(2, 'Java Basics', 2, 'Which data type is best suited for storing true or false values?', 'int', 'String', 'boolean', 'char', 'C', 'boolean stores only true or false values.'),
(3, 'Java Basics', 3, 'What is the output type of 5 / 2 in Java when both operands are integers?', '2.5', '2', '3', 'Compilation error', 'B', 'Integer division truncates the decimal part, so 5 / 2 becomes 2.'),
(4, 'OOP', 1, 'Which OOP principle hides internal data using private fields and public methods?', 'Polymorphism', 'Abstraction', 'Encapsulation', 'Inheritance', 'C', 'Encapsulation protects data and exposes behavior through methods.'),
(5, 'OOP', 2, 'What is the main purpose of an abstract class?', 'To store only constants', 'To prevent object creation of incomplete designs', 'To replace interfaces completely', 'To avoid inheritance', 'B', 'Abstract classes provide partial implementation and cannot be instantiated directly.'),
(6, 'OOP', 3, 'Which statement about method overriding is correct?', 'It requires static methods', 'It happens when a subclass provides its own version of a parent method', 'It is only possible with private methods', 'It removes inheritance', 'B', 'Overriding lets a subclass redefine inherited behavior with the same signature.'),
(7, 'Operating System', 1, 'Which operating system component decides which process gets the CPU next?', 'File manager', 'Scheduler', 'Loader', 'Device driver', 'B', 'The scheduler selects the next process based on the scheduling algorithm.'),
(8, 'Operating System', 2, 'What is the main purpose of virtual memory?', 'To replace the CPU', 'To increase monitor resolution', 'To give processes the illusion of larger memory space', 'To compress files automatically', 'C', 'Virtual memory uses disk space to extend apparent main memory.'),
(9, 'Operating System', 3, 'Which problem occurs when two or more processes wait forever for resources held by each other?', 'Thrashing', 'Fragmentation', 'Deadlock', 'Starvation', 'C', 'Deadlock happens when circular waiting prevents progress.'),
(10, 'Computer Networks', 1, 'Which device forwards packets between different networks?', 'Hub', 'Switch', 'Router', 'Repeater', 'C', 'A router connects networks and forwards packets using IP addresses.'),
(11, 'Computer Networks', 2, 'What does HTTP primarily define?', 'How operating systems boot', 'Rules for transferring web resources', 'How databases normalize tables', 'How processors schedule tasks', 'B', 'HTTP is the protocol used to transfer web pages and related resources.'),
(12, 'Computer Networks', 3, 'Which layer of the OSI model is responsible for end-to-end communication and reliability?', 'Physical', 'Transport', 'Data Link', 'Network', 'B', 'The transport layer handles end-to-end delivery, including reliability in protocols like TCP.'),
(13, 'Exception Handling', 1, 'Which block always executes whether an exception occurs or not?', 'catch', 'finally', 'throw', 'try', 'B', 'finally is designed for cleanup work that must always run.'),
(14, 'Exception Handling', 2, 'Which keyword is used to create a custom exception object?', 'catch', 'throws', 'throw', 'new', 'D', 'A custom exception object is instantiated using new before being thrown.'),
(15, 'Exception Handling', 3, 'Which is true about checked exceptions?', 'They are ignored by the compiler', 'They must be handled or declared', 'They only happen in threads', 'They can only be runtime exceptions', 'B', 'Checked exceptions require explicit handling or declaration.'),
(16, 'Data Structures', 1, 'Which data structure follows the Last In First Out principle?', 'Queue', 'Stack', 'Linked List', 'Tree', 'B', 'A stack removes the most recently inserted item first.'),
(17, 'Data Structures', 2, 'What is the average-case time complexity of binary search on a sorted array?', 'O(n)', 'O(log n)', 'O(n log n)', 'O(1)', 'B', 'Binary search halves the search space on each step, giving logarithmic complexity.'),
(18, 'Data Structures', 3, 'Which traversal visits the root node between the left and right subtrees in a binary tree?', 'Preorder', 'Postorder', 'Inorder', 'Level order', 'C', 'Inorder traversal processes left subtree, root, then right subtree.'),
(19, 'Web Development', 1, 'Which language is primarily used to structure content on web pages?', 'CSS', 'JavaScript', 'HTML', 'SQL', 'C', 'HTML defines the structure and content of a webpage.'),
(20, 'Web Development', 2, 'What is the main role of CSS in web development?', 'To store records', 'To style and layout web pages', 'To compile Java code', 'To manage network routing', 'B', 'CSS controls the presentation, layout, and design of HTML content.'),
(21, 'Web Development', 3, 'Which HTTP method is commonly used to submit form data that creates a new resource?', 'GET', 'POST', 'DELETE', 'TRACE', 'B', 'POST is typically used when submitting form data that creates or changes server-side data.');

INSERT INTO quizzes (id, user_id, is_revision, targeted_difficulty, generated_at) VALUES
(1, 2, FALSE, 'Foundation', NOW() - INTERVAL 4 DAY),
(2, 2, FALSE, 'Intermediate', NOW() - INTERVAL 2 DAY);

INSERT INTO quiz_attempts (quiz_id, user_id, question_id, selected_option, is_correct, response_time_seconds, attempted_at) VALUES
(1, 2, 1, 'B', TRUE, 11, NOW() - INTERVAL 4 DAY),
(1, 2, 4, 'C', TRUE, 12, NOW() - INTERVAL 4 DAY),
(1, 2, 7, 'A', FALSE, 22, NOW() - INTERVAL 4 DAY),
(1, 2, 10, 'B', FALSE, 18, NOW() - INTERVAL 4 DAY),
(1, 2, 16, 'A', FALSE, 18, NOW() - INTERVAL 4 DAY),
(1, 2, 19, 'C', TRUE, 13, NOW() - INTERVAL 4 DAY),
(1, 2, 13, 'B', TRUE, 14, NOW() - INTERVAL 4 DAY),
(2, 2, 2, 'A', FALSE, 24, NOW() - INTERVAL 2 DAY),
(2, 2, 5, 'B', TRUE, 19, NOW() - INTERVAL 2 DAY),
(2, 2, 8, 'B', TRUE, 16, NOW() - INTERVAL 2 DAY),
(2, 2, 11, 'B', TRUE, 17, NOW() - INTERVAL 2 DAY),
(2, 2, 17, 'B', TRUE, 15, NOW() - INTERVAL 2 DAY),
(2, 2, 20, 'A', FALSE, 21, NOW() - INTERVAL 2 DAY),
(2, 2, 14, 'D', TRUE, 20, NOW() - INTERVAL 2 DAY);

INSERT INTO performance (user_id, topic, accuracy, speed, consistency, composite_score, average_response_time, attempts, last_updated) VALUES
(2, 'Java Basics', 0.50, 0.42, 0.50, 0.48, 17.5, 2, NOW()),
(2, 'OOP', 1.00, 0.48, 1.00, 0.90, 15.5, 2, NOW()),
(2, 'Operating System', 0.50, 0.37, 0.50, 0.47, 19.0, 2, NOW()),
(2, 'Computer Networks', 0.50, 0.42, 0.50, 0.48, 17.5, 2, NOW()),
(2, 'Exception Handling', 1.00, 0.43, 1.00, 0.89, 17.0, 2, NOW()),
(2, 'Data Structures', 0.50, 0.45, 0.50, 0.49, 16.5, 2, NOW()),
(2, 'Web Development', 0.50, 0.43, 0.50, 0.49, 17.0, 2, NOW());
