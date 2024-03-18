/* REXX */
say "I'm thinking of a number between 1 and 10."
secret = RANDOM(1,10)
tries = 1

do while (guess \= secret)
    say "What is your guess?"
    pull guess
    if (guess \= secret) then
    do
        say "That's not it. Try again"
        tries = tries + 1
    end
end
say "You got it! And it only took you" tries "tries!"
exit
