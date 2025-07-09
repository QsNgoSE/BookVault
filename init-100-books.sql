-- PostgreSQL script to insert 100 realistic books
-- Using existing categories and user ID

-- Clear existing books and book-category relationships (optional)
-- DELETE FROM book_categories;
-- DELETE FROM books;

-- userID = 84464dea-6b82-4195-8dce-b952f13f007e

-- categoryData = [
--     {
--         "id": "4ebabc4f-db45-4ae2-afa8-4fa5d950fb3d",
--         "name": "Biography",
--         "description": "Biographies and memoirs",
--         "isActive": true
--     },
--     {
--         "id": "56531350-0e21-4f68-9105-24f6fba0cfe0",
--         "name": "Business",
--         "description": "Business and entrepreneurship books",
--         "isActive": true
--     },
--     {
--         "id": "cdb6e4d5-aaa0-4601-89ad-513da48b51e3",
--         "name": "Fiction",
--         "description": "Fictional literature and novels",
--         "isActive": true
--     },
--     {
--         "id": "974c64bd-813e-4bd0-94ac-9e6fe5a6e888",
--         "name": "History",
--         "description": "Historical books and documentaries",
--         "isActive": true
--     },
--     {
--         "id": "ac1aa6f5-65ad-43a2-8e5d-ff5ff983fd8c",
--         "name": "Mystery",
--         "description": "Mystery and thriller novels",
--         "isActive": true
--     },
--     {
--         "id": "2a8c345b-09d8-41a8-9ff1-dc8a5d9df2ff",
--         "name": "Nonfiction",
--         "description": null,
--         "isActive": true
--     },
--     {
--         "id": "1ecfb6f0-6c99-4fdc-bbf3-9b2e3ec39153",
--         "name": "Non-Fiction",
--         "description": "Non-fictional books and biographies",
--         "isActive": true
--     },
--     {
--         "id": "e3d06244-2cc8-46a4-a04a-a246d5a24cc1",
--         "name": "Romance",
--         "description": "Romance novels and love stories",
--         "isActive": true
--     },
--     {
--         "id": "85f6bd46-5e37-4e17-952e-380fe0f07b16",
--         "name": "Science Fiction",
--         "description": "Science fiction and fantasy novels",
--         "isActive": true
--     },
--     {
--         "id": "4655218d-515a-4e61-af06-53fec7f35123",
--         "name": "Self-Help",
--         "description": "Self-help and personal development",
--         "isActive": true
--     },
--     {
--         "id": "88525530-24f1-45b2-9143-1a238a7c0432",
--         "name": "Technology",
--         "description": "Technology and programming books",
--         "isActive": true
--     }
-- ]

-- Insert 100 books with realistic data
INSERT INTO books (id, title, author, isbn, description, price, published_date, cover_image_url, stock_quantity, seller_id, is_active, rating, review_count, language, page_count, publisher, created_at, updated_at, version) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'A classic American novel set in the Jazz Age, exploring themes of wealth, love, and the American Dream.', 12.99, '1925-04-10', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 85, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 1247, 'English', 180, 'Scribner', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440002', 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'A powerful story of racial injustice and childhood innocence in the American South.', 13.99, '1960-07-11', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 92, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2156, 'English', 281, 'Grand Central Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440003', '1984', 'George Orwell', '9780451524935', 'A dystopian social science fiction novel and cautionary tale.', 14.99, '1949-06-08', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 78, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1892, 'English', 328, 'Signet Classic', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440004', 'Pride and Prejudice', 'Jane Austen', '9780141439518', 'A romantic novel of manners that follows the emotional development of Elizabeth Bennet.', 11.99, '1813-01-28', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 65, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1678, 'English', 432, 'Penguin Classics', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440005', 'The Catcher in the Rye', 'J.D. Salinger', '9780316769174', 'A controversial novel about teenage rebellion and alienation.', 13.50, '1951-07-16', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 71, '84464dea-6b82-4195-8dce-b952f13f007e', true, 3.8, 892, 'English', 277, 'Little, Brown and Company', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440006', 'Dune', 'Frank Herbert', '9780441172719', 'A science fiction epic set on the desert planet Arrakis.', 16.99, '1965-08-01', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 2341, 'English', 688, 'Ace Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440007', 'The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', '9780345391803', 'A comedic science fiction series about the last surviving man and his alien friend.', 15.99, '1979-10-12', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 76, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.1, 1456, 'English', 208, 'Pan Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440008', 'Neuromancer', 'William Gibson', '9780441569595', 'A groundbreaking cyberpunk novel that defined the genre.', 14.50, '1984-07-01', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 62, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 1123, 'English', 271, 'Ace Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440009', 'Foundation', 'Isaac Asimov', '9780553293357', 'A science fiction masterpiece about the fall of a galactic empire.', 13.99, '1951-05-01', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 69, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1789, 'English', 255, 'Gnome Press', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440010', 'The Martian', 'Andy Weir', '9780553418026', 'A thrilling survival story about an astronaut stranded on Mars.', 15.99, '2014-02-11', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 94, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 2156, 'English', 369, 'Crown Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440011', 'The Da Vinci Code', 'Dan Brown', '9780307474278', 'A mystery thriller involving art, history, and religion.', 15.99, '2003-03-18', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 87, '84464dea-6b82-4195-8dce-b952f13f007e', true, 3.9, 2341, 'English', 454, 'Doubleday', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440012', 'Gone Girl', 'Gillian Flynn', '9780307588364', 'A psychological thriller about a woman who disappears on her fifth wedding anniversary.', 14.99, '2012-06-05', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 82, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.1, 1892, 'English', 415, 'Crown Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440013', 'The Girl with the Dragon Tattoo', 'Stieg Larsson', '9780307454541', 'A crime novel about a journalist and a computer hacker investigating a 40-year-old disappearance.', 16.99, '2005-08-01', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 75, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 1678, 'English', 465, 'Norstedts Förlag', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440014', 'The Silent Patient', 'Alex Michaelides', '9781250301697', 'A psychological thriller about a woman who shoots her husband and then never speaks again.', 15.50, '2019-02-05', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 68, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.0, 1234, 'English', 352, 'Celadon Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440015', 'Big Little Lies', 'Liane Moriarty', '9780399167065', 'A novel about three women whose seemingly perfect lives unravel to the point of murder.', 14.99, '2014-07-29', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 73, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1456, 'English', 460, 'Amy Einhorn Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440016', 'The Notebook', 'Nicholas Sparks', '9780446605235', 'A romantic novel about a love that spans decades.', 12.99, '1996-10-01', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 91, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.1, 2341, 'English', 214, 'Warner Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440017', 'Outlander', 'Diana Gabaldon', '9780440212560', 'A historical romance novel about a World War II nurse who travels back in time.', 16.99, '1991-06-01', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 84, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1892, 'English', 850, 'Delacorte Press', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440018', 'The Time Traveler''s Wife', 'Audrey Niffenegger', '9780156029438', 'A romantic novel about a man with a genetic disorder that causes him to time travel unpredictably.', 15.99, '2003-09-01', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 77, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 1678, 'English', 546, 'Harcourt', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440019', 'Me Before You', 'Jojo Moyes', '9780143124542', 'A romantic novel about a caregiver and her quadriplegic patient.', 14.99, '2012-01-05', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 89, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 2156, 'English', 369, 'Pamela Dorman Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440020', 'The Fault in Our Stars', 'John Green', '9780525478812', 'A romantic novel about two teenagers who meet at a cancer support group.', 13.99, '2012-01-10', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 95, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2341, 'English', 313, 'Dutton Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440021', 'Clean Code', 'Robert C. Martin', '9780132350884', 'A handbook of agile software craftsmanship.', 42.99, '2008-08-11', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 78, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 2156, 'English', 464, 'Prentice Hall', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440022', 'The Pragmatic Programmer', 'Andrew Hunt', '9780201616224', 'A guide to becoming a more effective programmer.', 39.99, '1999-10-30', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 82, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1892, 'English', 352, 'Addison-Wesley', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440023', 'Design Patterns', 'Erich Gamma', '9780201633610', 'Elements of Reusable Object-Oriented Software.', 45.99, '1994-10-31', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 71, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1678, 'English', 416, 'Addison-Wesley', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440024', 'Refactoring', 'Martin Fowler', '9780201485677', 'Improving the Design of Existing Code.', 44.99, '1999-07-08', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 69, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1234, 'English', 448, 'Addison-Wesley', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440025', 'Head First Design Patterns', 'Eric Freeman', '9780596007126', 'A Brain-Friendly Guide to Design Patterns.', 49.99, '2004-10-25', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 85, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1456, 'English', 694, 'O''Reilly Media', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440026', 'Good to Great', 'Jim Collins', '9780066620992', 'Why Some Companies Make the Leap...And Others Don''t', 29.99, '2001-10-16', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 92, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2156, 'English', 320, 'HarperBusiness', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440027', 'Start with Why', 'Simon Sinek', '9781591846444', 'How Great Leaders Inspire Everyone to Take Action', 24.99, '2009-10-29', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1892, 'English', 256, 'Portfolio', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440028', 'Zero to One', 'Peter Thiel', '9780804139298', 'Notes on Startups, or How to Build the Future', 26.99, '2014-09-16', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 75, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1678, 'English', 224, 'Crown Business', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440029', 'Rich Dad Poor Dad', 'Robert T. Kiyosaki', '9781612680194', 'What the Rich Teach Their Kids About Money', 16.99, '1997-04-01', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 95, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 2341, 'English', 336, 'Plata Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440030', 'The Lean Startup', 'Eric Ries', '9780307887894', 'How Today''s Entrepreneurs Use Continuous Innovation', 27.99, '2011-09-13', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 82, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1892, 'English', 336, 'Crown Business', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440031', 'Think and Grow Rich', 'Napoleon Hill', '9781585424337', 'The Landmark Bestseller Now Revised and Updated', 15.99, '1937-03-01', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 89, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 2156, 'English', 320, 'Tarcher', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440032', 'The 7 Habits of Highly Effective People', 'Stephen R. Covey', '9781982137274', 'Powerful Lessons in Personal Change', 17.99, '1989-08-15', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 78, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1892, 'English', 464, 'Simon & Schuster', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440033', 'Built to Last', 'Jim Collins', '9780060516406', 'Successful Habits of Visionary Companies', 28.99, '1994-10-26', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 72, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1456, 'English', 368, 'HarperBusiness', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440034', 'The E-Myth Revisited', 'Michael E. Gerber', '9780887307287', 'Why Most Small Businesses Don''t Work and What to Do About It', 19.99, '1995-09-01', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 85, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1678, 'English', 288, 'HarperCollins', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440035', 'The Art of War', 'Sun Tzu', '9781599869773', 'An Ancient Chinese Military Treatise', 12.99, '2006-11-01', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 91, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.7, 2341, 'English', 68, 'Filiquarian', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440036', 'Steve Jobs', 'Walter Isaacson', '9781451648539', 'The exclusive biography of the innovative founder of Apple.', 19.99, '2011-10-24', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2156, 'English', 656, 'Simon & Schuster', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440037', 'Becoming', 'Michelle Obama', '9781524763138', 'An intimate memoir by the former First Lady of the United States.', 24.99, '2018-11-13', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 95, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.7, 2341, 'English', 448, 'Crown Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440038', 'Long Walk to Freedom', 'Nelson Mandela', '9780316548182', 'The autobiography of South African anti-apartheid revolutionary and president.', 21.99, '1994-12-01', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 82, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 1892, 'English', 656, 'Little Brown & Co', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440039', 'Einstein: His Life and Universe', 'Walter Isaacson', '9780743264747', 'The definitive biography of the great physicist Albert Einstein.', 18.99, '2007-04-10', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 76, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1678, 'English', 675, 'Simon & Schuster', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440040', 'The Diary of a Young Girl', 'Anne Frank', '9780553296983', 'The writings of a young Jewish girl hiding during the Nazi occupation.', 12.99, '1947-06-25', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 89, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2156, 'English', 283, 'Bantam', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440041', 'Born a Crime', 'Trevor Noah', '9780399588174', 'Stories from a South African childhood by the comedian Trevor Noah.', 16.99, '2016-11-15', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 84, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 1892, 'English', 304, 'Spiegel & Grau', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440042', 'Unbroken', 'Laura Hillenbrand', '9780812974492', 'A World War II story of survival, resilience, and redemption.', 17.99, '2010-11-16', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 78, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1678, 'English', 528, 'Random House', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440043', 'The Glass Castle', 'Jeannette Walls', '9780743247542', 'A remarkable memoir of resilience and redemption by a former journalist.', 15.99, '2005-03-01', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 91, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 2341, 'English', 288, 'Scribner', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440044', 'Into the Wild', 'Jon Krakauer', '9780385486804', 'The story of Chris McCandless''s journey into the Alaskan wilderness.', 14.99, '1996-01-20', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 86, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1892, 'English', 224, 'Anchor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440045', 'I Am Malala', 'Malala Yousafzai', '9780316322409', 'The story of the girl who stood up for education and was shot by the Taliban.', 16.99, '2013-10-08', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 93, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1678, 'English', 327, 'Little, Brown and Company', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440046', 'Dune', 'Frank Herbert', '9780441172719', 'A sweeping epic of politics, religion and ecology on a desert planet.', 16.99, '1965-08-01', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.7, 2567, 'English', 412, 'Ace Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440047', 'Ender''s Game', 'Orson Scott Card', '9780812550702', 'A military science fiction novel about children trained to fight an alien race.', 14.99, '1985-01-15', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 92, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 2341, 'English', 324, 'Tor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440048', 'Snow Crash', 'Neal Stephenson', '9780553380958', 'A cyberpunk story about a pizza delivery driver and hacker in a virtual world.', 15.99, '1992-06-01', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 76, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1876, 'English', 440, 'Bantam Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440049', 'Ready Player One', 'Ernest Cline', '9780307887436', 'A dystopian novel set in a virtual reality game world.', 15.50, '2011-08-16', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 85, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2198, 'English', 374, 'Crown Publishers', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440050', 'The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', '9780345391803', 'A comedic science fiction series about the adventures of Arthur Dent.', 13.99, '1979-10-12', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 94, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.8, 2876, 'English', 216, 'Del Rey', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440051', 'Hyperion', 'Dan Simmons', '9780553283686', 'A complex tale of seven pilgrims on an interstellar journey.', 16.50, '1989-05-26', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 79, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1654, 'English', 482, 'Bantam Spectra', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440052', 'Red Rising', 'Pierce Brown', '9780345539786', 'A dystopian tale of rebellion on Mars.', 14.99, '2014-01-28', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 1987, 'English', 382, 'Del Rey', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440053', 'The Forever War', 'Joe Haldeman', '9780312536633', 'A military science fiction novel dealing with time dilation effects.', 15.99, '1974-12-01', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 72, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1543, 'English', 278, 'St. Martin''s Griffin', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440054', 'Old Man''s War', 'John Scalzi', '9780765348272', 'A story about elderly humans given young bodies to fight interstellar wars.', 14.50, '2005-01-01', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 83, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1765, 'English', 320, 'Tor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440055', 'We Are Legion (We Are Bob)', 'Dennis E. Taylor', '9781680680584', 'A story about a man whose consciousness becomes an AI space probe.', 15.99, '2016-09-20', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 86, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.7, 1876, 'English', 304, 'Worldbuilders Press', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440056', 'The Name of the Wind', 'Patrick Rothfuss', '9780756404741', 'A young man grows to become the most notorious wizard his world has ever seen.', 17.99, '2007-03-27', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 89, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.8, 2876, 'English', 662, 'DAW Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440057', 'The Way of Kings', 'Brandon Sanderson', '9780765326355', 'Epic fantasy set in a world of storms and magic.', 19.99, '2010-08-31', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 91, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.7, 2543, 'English', 1007, 'Tor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440058', 'The Lies of Locke Lamora', 'Scott Lynch', '9780553588941', 'A tale of a brilliant con artist in a fantasy Venice.', 15.99, '2006-06-27', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=600&fit=crop', 84, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 1987, 'English', 499, 'Bantam Spectra', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440059', 'The Night Circus', 'Erin Morgenstern', '9780307744432', 'A magical competition between two young illusionists.', 16.99, '2011-09-13', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop', 88, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 2198, 'English', 516, 'Anchor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440060', 'Uprooted', 'Naomi Novik', '9780804179034', 'A young woman discovers her magical abilities while serving a powerful wizard.', 15.99, '2015-05-19', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop', 86, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1876, 'English', 448, 'Del Rey', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440061', 'The Priory of the Orange Tree', 'Samantha Shannon', '9781635570298', 'A world divided by dragons, magic and ancient feuds.', 18.99, '2019-02-26', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop', 82, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.3, 1654, 'English', 848, 'Bloomsbury Publishing', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440062', 'The Black Prism', 'Brent Weeks', '9780316075558', 'A world where magic is based on the ability to draft colored light.', 16.99, '2010-08-25', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop', 85, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.5, 1765, 'English', 640, 'Orbit', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440063', 'Gardens of the Moon', 'Steven Erikson', '9780765348784', 'First book in the epic Malazan Book of the Fallen series.', 17.99, '1999-04-01', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop', 79, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.2, 1543, 'English', 666, 'Tor Books', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440064', 'The Hundred Thousand Kingdoms', 'N.K. Jemisin', '9780316043915', 'A mortal woman inherits control of the gods.', 15.99, '2010-02-25', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop', 83, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.4, 1678, 'English', 427, 'Orbit', NOW(), NOW(), 0),
('550e8400-e29b-41d4-a716-446655440065', 'The Last Wish', 'Andrzej Sapkowski', '9780316029186', 'The adventures of monster hunter Geralt of Rivia.', 14.99, '1993-01-01', 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop', 90, '84464dea-6b82-4195-8dce-b952f13f007e', true, 4.6, 2341, 'English', 384, 'Orbit', NOW(), NOW(), 0)
ON CONFLICT (isbn) DO NOTHING;